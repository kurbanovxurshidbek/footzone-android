package com.footzone.footzone.ui.fragments.worktime

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.View
import android.widget.NumberPicker
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentChooseWorkTimeBinding
import com.footzone.footzone.model.WorkingDay
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.WORK_TIME
import com.footzone.footzone.utils.KeyValues.WORK_TIMES
import java.time.LocalTime
import java.util.*

class ChooseWorkTimeFragment : BaseFragment(R.layout.fragment_choose_work_time) {
    lateinit var binding: FragmentChooseWorkTimeBinding
    private val workTimes = ArrayList<WorkingDay>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseWorkTimeBinding.bind(view)
        initViews()
    }

    private fun initViews() {

        binding.apply {
            icClose.setOnClickListener { requireActivity().onBackPressed() }
            tvCancel.setOnClickListener { requireActivity().onBackPressed() }
            tvSelection.setOnClickListener {
                setFragmentResult(KeyValues.TYPE_WORK_TIME,
                    bundleOf(WORK_TIMES to workTimes, WORK_TIME to wortTime()))
                requireActivity().onBackPressed()
            }
        }



        binding.switchMo.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutMo, isOn)
        }

        binding.switchTu.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutTu, isOn)
        }

        binding.switchWe.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutWe, isOn)
        }

        binding.switchTh.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutTh, isOn)
        }
        binding.switchFr.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutFr, isOn)
        }

        binding.switchSa.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutSa, isOn)
        }

        binding.switchSu.setOnToggledListener { toggleableView, isOn ->
            openTime(binding.inputLayoutSu, isOn)
        }

        numberPicker(binding.startTimeMo, binding.finishTimeMo)
        numberPicker(binding.startTimeTu, binding.finishTimeTu)
        numberPicker(binding.startTimeWe, binding.finishTimeWe)
        numberPicker(binding.startTimeTh, binding.finishTimeTh)
        numberPicker(binding.startTimeFr, binding.finishTimeFr)
        numberPicker(binding.startTimeSa, binding.finishTimeSa)
        numberPicker(binding.startTimeSu, binding.finishTimeSu)

    }

    fun openTime(layout: RelativeLayout, bool: Boolean) {
        if (bool) {
            layout.visibility = View.VISIBLE
        } else {
            layout.visibility = View.GONE
        }
    }

    fun numberPicker(startTime: NumberPicker, finishTime: NumberPicker) {
        val timeList = resources.getStringArray(R.array.timelist)
        startTime.minValue = 1
        startTime.maxValue = 48
        startTime.displayedValues = timeList
        finishTime.minValue = 1
        finishTime.maxValue = 48
        finishTime.displayedValues = timeList

        startTime.setOnScrollListener { numberPicker, i ->
            checkVibrationIsOn(requireContext())
        }


        finishTime.setOnScrollListener { numberPicker, i ->
            checkVibrationIsOn(requireContext())
        }
    }

    fun wortTime(): String {
        var string = ""
        if (binding.switchMo.isOn) {
            string += "Du, "
            addTime(binding.startTimeMo, binding.finishTimeMo, "MONDAY")
        }

        if (binding.switchTu.isOn) {
            string += "Se, "
            addTime(binding.startTimeTu, binding.finishTimeTu, "TUESDAY")
        }

        if (binding.switchWe.isOn) {
            string += "Cho, "
            addTime(binding.startTimeWe, binding.finishTimeWe, "WEDNESDAY")
        }

        if (binding.switchTh.isOn) {
            string += "Pa, "
            addTime(binding.startTimeTh, binding.finishTimeTh, "THURSDAY")
        }

        if (binding.switchFr.isOn) {
            string += "Ju, "
            addTime(binding.startTimeFr, binding.finishTimeFr, "FRIDAY")
        }

        if (binding.switchSa.isOn) {
            string += "Sha, "
            addTime(binding.startTimeSa, binding.finishTimeSa, "SATURDAY")
        }

        if (binding.switchSu.isOn) {
            string += "Ya"
            addTime(binding.startTimeSu, binding.finishTimeSu, "SUNDAY")
        }

        return string
    }

    private fun addTime(startTime: NumberPicker, finishTime: NumberPicker, day: String) {
        val timeList = resources.getStringArray(R.array.timelist)
        val startTime = timeList[startTime.value - 1].toString()
        val finishTime = timeList[finishTime.value - 1].toString()
        workTimes.add(WorkingDay(day, finishTime, startTime))
    }

    /**
     * this function, gives the NumberPicker sound and vibrate
     */
    fun checkVibrationIsOn(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
            val v: Vibrator =
                requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(70)
        } else {
            val mMediaPlayer = MediaPlayer.create(context, R.raw.mouse_1)
            val audioManager =
                requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.start()

            val handler = Handler()
            val t = Timer()
            t.schedule(object : TimerTask() {
                override fun run() {
                    handler.post {
                        mMediaPlayer.stop()
                    }
                }
            }, 500)
        }
    }
}