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
import com.footzone.footzone.model.addstadium.WorkingDay
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import java.time.LocalTime
import java.util.*

class ChooseWorkTimeFragment : BaseFragment(R.layout.fragment_choose_work_time) {
    lateinit var binding: FragmentChooseWorkTimeBinding

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
                val workTimes = ArrayList<WorkingDay>()

                setFragmentResult(KeyValues.TYPE_WORK_TIME, bundleOf("bundleKey" to workTimes))
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
        startTime.maxValue = 47
        startTime.displayedValues = timeList
        finishTime.minValue = 1
        finishTime.maxValue = 47
        finishTime.displayedValues = timeList
    }

    fun wortTime(): String {
        var string = ""
        if (binding.switchMo.isOn) {
            string += "Du, "
        }

        if (binding.switchTu.isOn) {
            string += "Se, "
        }

        if (binding.switchWe.isOn) {
            string += "Cho, "
        }

        if (binding.switchTh.isOn) {
            string += "Pa, "
        }

        if (binding.switchFr.isOn) {
            string += "Ju, "
        }

        if (binding.switchSa.isOn) {
            string += "Sha, "
        }

        if (binding.switchSu.isOn) {
            string += "Ya"
        }

        return string
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
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.start()

            val handler = Handler()
            val t = Timer()
            t.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Runnable {
                        mMediaPlayer.stop()
                    })
                }
            }, 500)
        }
    }
}