package com.example.pulse.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.pulse.R
import com.example.pulse.TinyDB
import com.example.pulse.adapters.CardAdapter
import com.example.pulse.databinding.GeneralPageFragmentBinding
import com.example.pulse.viewModel.GeneralPageViewModel
import androidx.lifecycle.Observer
import com.example.pulse.viewModel.GeneralPageViewModel.Companion.arrayDateGraph
import com.example.pulse.viewModel.GeneralPageViewModel.Companion.arrayPulseGraph
import com.example.pulse.viewModel.StatisticsViewModel.Companion.counterSts
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartSymbolType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import im.dacer.androidcharts.LineView
import java.text.SimpleDateFormat
import java.util.*

class GeneralPage : Fragment() {

    companion object {
        fun newInstance() = GeneralPage()
        lateinit var bindingGeneralPage: GeneralPageFragmentBinding
        lateinit var tinyDB: TinyDB
    }

    var chipsOtherCheck = arrayListOf<String>()
    var dateVM = ""
    var daysVM = 0
    var monthVM = 0
    var yearsVM = 0
    var hoursVM = 0
    var minuteVM = 0
    var pulseVM = 60
    lateinit var timePicker: TimePickerDialog
    lateinit var datePicker: DatePickerDialog
    var calendar: Calendar = Calendar.getInstance()
    val viewModel: GeneralPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingGeneralPage= DataBindingUtil.inflate(inflater, R.layout.general_page_fragment,container,false)
        return bindingGeneralPage.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindingGeneralPage.apply {

            scrollGraph.post {
                scrollGraph.fullScroll(View.FOCUS_RIGHT)
            }

            createChipGroup(
                type = TypeChips.Healthy(),
                chipGroup = chipGroupHealthy,
                list = viewModel.getHealthyList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Unhealthy(),
                chipGroup = chipGroupUnhealthy,
                list = viewModel.getUnhealthyList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Symptoms(),
                chipGroup = chipGroupSymptoms,
                list = viewModel.getSymptomsList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Care(),
                chipGroup = chipGroupCare,
                list = viewModel.getCareList(requireContext())
            )
            createChipGroup(
                type = TypeChips.Other(),
                chipGroup = chipGroupOtherTags,
                list = tinyDB.getListString("OtherChips")
            )

            txtRecord.setOnClickListener {
                datePicker.show()
            }

            //получаем сохраненный пульс
            if(tinyDB.getInt("nPicker") == 0) {
                nPicker.value = 60
            }else{
                nPicker.value = tinyDB.getInt("nPicker")
               }

            nPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                tinyDB.putInt("nPicker", nPicker.value)
            }

            //наблюдатель обновление графика
            viewModel.counter.observe(viewLifecycleOwner, Observer{
                graph(scrollGraph, txtOnbord)
            })

            //текст на кнопке сохранить
            btnSaveText(btnSave, requireContext())

            //невидимая кнопка для удаления карточки - костыль
            deleteCard.setOnClickListener {
                graph(scrollGraph, txtOnbord)
            }

            //получение графика
            viewModel.readDB()


            btnSave.setOnClickListener {
                CardAdapter.deleteCard = false

                //добавляем сохраненные данные
                chipsOtherCheck = tinyDB.getListString("OtherChips")

                val healthySelectedItems: List<String> = getSelectedItems(chipGroupHealthy)
                val unhealthySelectedItems: List<String> = getSelectedItems(chipGroupUnhealthy)
                val symptomsSelectedItems: List<String> = getSelectedItems(chipGroupSymptoms)
                val careSelectedItems: List<String> = getSelectedItems(chipGroupCare)
                val otherSelectedItems: List<String> = getSelectedItems(chipGroupOtherTags)

                //заполнение бд
                dateVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "")
                pulseVM = nPicker.value
                daysVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(".")?.get(0).toInt()
                monthVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(".")?.get(1).toInt()
                yearsVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(".")?.get(2).dropLast(5).replace(" ", "").toInt()
                hoursVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(" ")?.get(1).dropLast(2).replace(":", "").toInt()
                minuteVM = txtRecord.text.toString().replace("${context?.getString(R.string.record)} ", "").split(":")?.get(1).toInt()
                tinyDB.putInt("idDB", tinyDB.getInt("idDB")+1)

                viewModel.addDB(requireContext(), dateVM, pulseVM,
                    healthySelectedItems,
                    unhealthySelectedItems,
                    symptomsSelectedItems,
                    careSelectedItems, daysVM, monthVM, yearsVM, hoursVM, minuteVM, otherSelectedItems, tinyDB.getInt("idDB"))

                //обновляем график
                viewModel.readDB()

                chipGroupHealthy.clearCheck()
                chipGroupUnhealthy.clearCheck()
                chipGroupSymptoms.clearCheck()
                chipGroupCare.clearCheck()

                Toast.makeText(requireContext(), requireContext().getString(R.string.toastSave), Toast.LENGTH_SHORT).show()
                scrollGeneral.post {
                    scrollGeneral.fullScroll(View.FOCUS_UP)
                }
            }

            //фокус при пустой строке/скрытие клавы
            btnOtherTags.setOnClickListener {
                val inputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (editTxtOtherTags.text.isNotEmpty()) {
                    val chip = layoutInflater.inflate(
                        R.layout.chip,
                        chipGroupOtherTags,
                        false
                    ) as Chip
                    chip.isCheckable = true
                    chip.chipBackgroundColor =
                        resources.getColorStateList(R.drawable.chip_state_other)
                    chip.text = editTxtOtherTags.text.toString()
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        chipGroupOtherTags.removeView(chip)
                        chipsOtherCheck.remove(chip?.text)
                        tinyDB.putListString("OtherChips", chipsOtherCheck)
                    }
                    chipsOtherCheck.add("${chip?.text}")
                    chip.isChecked = true
                    chipGroupOtherTags.addView(chip)
                    tinyDB.putListString("OtherChips", chipsOtherCheck)
                    editTxtOtherTags.setText("")
                    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                } else {
                    editTxtOtherTags.requestFocus()
                }
            }

            //кнопка на клавиатуре = добавить
            editTxtOtherTags.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                var handled = false

                if (actionId == EditorInfo.IME_ACTION_DONE && editTxtOtherTags.text.toString() != "") {
                    btnOtherTags.callOnClick()
                    handled = true
                }
                handled
            })

            pickDate()
        }
    }

    //дейт/тайм пикеры
    private fun pickDate() {
        val timeListener =
            TimePickerDialog.OnTimeSetListener { view: TimePicker?, hour: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                updateDateAndTime()
            }
        val dateListener =
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, day: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                timePicker.show()
            }
        timePicker = TimePickerDialog(
            requireContext(),
            timeListener,
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            true
        )
        datePicker = DatePickerDialog(
            requireContext(),
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        bindingGeneralPage.txtRecord.text =
            "${requireContext().getString(R.string.record)} ${getCurrentDateAndTime()}"
    }

    fun updateDateAndTime() {
        bindingGeneralPage.txtRecord.text =
            "${requireContext().getString(R.string.record)} ${getCurrentDateAndTime()}"
    }

    fun getCurrentDateAndTime(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val currentDateAndTime = sdf.format(calendar.time)
        return currentDateAndTime
    }

    fun btnSaveText(btnSave: ExtendedFloatingActionButton, context: Context){
        btnSave.text = "${bindingGeneralPage.nPicker.value} ${context.getString(R.string.save)}"
    }

    fun graph(scrollGraph: HorizontalScrollView, txtOnbord: LinearLayout) {
        if (!tinyDB.getString("DateDB").isEmpty()) {

            counterSts.value?.let {
                counterSts.value = false
            }

            bindingGeneralPage.txtOnbord.visibility = View.GONE
            scrollGraph.visibility = View.VISIBLE

            val aaChartModel: AAChartModel = AAChartModel()
                .chartType(AAChartType.Spline)
                .backgroundColor("#FFFFFF")
                .dataLabelsEnabled(true)
                .legendEnabled(true)
                .animationDuration(10)
                .yAxisVisible(true)
                .yAxisLabelsEnabled(false)
                .yAxisTitle("")
                .markerSymbol(AAChartSymbolType.Circle)
                .colorsTheme(arrayOf("#29B6FC"))
                .yAxisMax(300)
                .categories(arrayDateGraph.toTypedArray())
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name(this.getString(R.string.pulse))
                            .data(arrayPulseGraph.toTypedArray())
                    )
                )
            bindingGeneralPage.aaChartView.aa_drawChartWithChartModel(aaChartModel)

            tinyDB.remove("DateDB")

            //фокус графика в конец
            scrollGraph.post {
                scrollGraph.fullScroll(View.FOCUS_RIGHT)
            }
        }
    }

    @SuppressLint("ResourceType")
    fun createChipGroup(type: TypeChips, chipGroup: ChipGroup, list: List<String>) {
        val chipBg = when (type) {
            is TypeChips.Care -> resources.getColorStateList(R.drawable.chip_state_care)
            is TypeChips.Healthy -> resources.getColorStateList(R.drawable.chip_state_healthy)
            is TypeChips.Symptoms -> resources.getColorStateList(R.drawable.chip_state_symptoms)
            is TypeChips.Unhealthy -> resources.getColorStateList(R.drawable.chip_state_unhealthy)
            is TypeChips.Other -> resources.getColorStateList(R.drawable.chip_state_other)
        }
        list.forEach {
            val chip = layoutInflater.inflate(
                R.layout.chip,
                chipGroup,
                false
            ) as Chip
            chip.chipBackgroundColor = chipBg
            chip.isCheckable = true
            chip.text = it
            chipGroup.addView(chip)
            if (chipBg == resources.getColorStateList(R.drawable.chip_state_other)){
                chip.isCloseIconVisible = true
            }
        }
    }

    fun getSelectedItems(chipGroup: ChipGroup): List<String> {
        return chipGroup.children.filter {
            (it as Chip).isChecked
        }.map {
            return@map (it as Chip).text.toString()
        }.toList()
    }
}

sealed class TypeChips() {
    class Healthy() : TypeChips()
    class Unhealthy() : TypeChips()
    class Symptoms() : TypeChips()
    class Care() : TypeChips()
    class Other() : TypeChips()
}
