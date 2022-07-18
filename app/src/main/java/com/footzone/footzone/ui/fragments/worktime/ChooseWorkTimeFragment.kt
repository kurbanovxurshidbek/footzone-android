package com.footzone.footzone.ui.fragments.worktime

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.NumberPicker
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.footzone.footzone.R
import com.footzone.footzone.databinding.FragmentChooseWorkTimeBinding
import com.footzone.footzone.model.WorkingDay
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.utils.KeyValues
import com.footzone.footzone.utils.KeyValues.WORK_TIME
import com.footzone.footzone.utils.KeyValues.WORK_TIMES
import com.footzone.footzone.utils.extensions.hide
import com.footzone.footzone.utils.extensions.show
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
            icClose.setOnClickListener {
                back()
            }
            tvCancel.setOnClickListener {
                back()
            }

            tvSelection.setOnClickListener {
                setFragmentResult(
                    KeyValues.TYPE_WORK_TIME,
                    bundleOf(WORK_TIMES to workTimes, WORK_TIME to wortTime())
                )
                back()
            }

            switchMo.setOnCheckedChangeListener { _, isOn ->
                openTime(inputLayoutMo, isOn, cardViewMo)
            }
            switchTu.setOnCheckedChangeListener { _, isOn ->
                openTime(inputLayoutTu, isOn, cardViewTu)
            }

            switchWe.setOnCheckedChangeListener { _, isOn ->
                openTime(inputLayoutWe, isOn, cardViewWe)
            }

            switchTh.setOnCheckedChangeListener { _, isOn ->
                openTime(inputLayoutTh, isOn, cardViewTh)
            }
            switchFr.setOnCheckedChangeListener { _, isOn ->
                openTime(inputLayoutFr, isOn, cardViewFr)
            }

            switchSa.setOnCheckedChangeListener { _, isOn ->
                openTime(inputLayoutSa, isOn, cardViewSa)
            }

            switchSu.setOnCheckedChangeListener { _, isOn ->
                openTime(inputLayoutSu, isOn, cardViewSu)
            }

            numberPicker(startTimeMo, finishTimeMo)
            numberPicker(startTimeTu, finishTimeTu)
            numberPicker(startTimeWe, finishTimeWe)
            numberPicker(startTimeTh, finishTimeTh)
            numberPicker(startTimeFr, finishTimeFr)
            numberPicker(startTimeSa, finishTimeSa)
            numberPicker(startTimeSu, finishTimeSu)
        }
    }

    private fun openTime(rel: RelativeLayout, bool: Boolean, cardView: CardView) = if (bool) {
       // layout.show()
        rel.setVisibility(View.VISIBLE)
        androidx.transition.TransitionManager.beginDelayedTransition(cardView,
            androidx.transition.AutoTransition())

    } else {
       // layout.hide()
        TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            rel.setVisibility(View.GONE)
    }

    private fun numberPicker(startTime: NumberPicker, finishTime: NumberPicker) {
        val timeList = resources.getStringArray(R.array.timelist)
        startTime.minValue = 1
        startTime.maxValue = 48
        startTime.displayedValues = timeList
        finishTime.minValue = 1
        finishTime.maxValue = 48
        finishTime.displayedValues = timeList

        startTime.setOnScrollListener { _, _ ->
            checkVibrationIsOn(requireContext())
        }


        finishTime.setOnScrollListener { _, _ ->
            checkVibrationIsOn(requireContext())
        }
    }

    private fun wortTime(): String {

        val array = resources.getStringArray(R.array.daysWeek)
        var string = ""

        binding.apply {
            if (switchMo.isChecked) {
                string += "Du, "
                addTime(startTimeMo, finishTimeMo, array[1])
            }

            if (switchTu.isChecked) {
                string += "Se, "
                addTime(startTimeTu, finishTimeTu, array[2])
            }

            if (switchWe.isChecked) {
                string += "Cho, "
                addTime(startTimeWe, finishTimeWe, array[3])
            }

            if (switchTh.isChecked) {
                string += "Pa, "
                addTime(startTimeTh, finishTimeTh, array[4])
            }

            if (switchFr.isChecked) {
                string += "Ju, "
                addTime(startTimeFr, finishTimeFr, array[5])
            }

            if (switchSa.isChecked) {
                string += "Sha, "
                addTime(startTimeSa, finishTimeSa, array[6])
            }

            if (switchSu.isChecked) {
                string += "Ya"
                addTime(startTimeSu, finishTimeSu, array[0])
            }
        }

        return string
    }

    private fun addTime(startTime: NumberPicker, finishTime: NumberPicker, day: String) {
        val timeList = resources.getStringArray(R.array.timelist)
        val start = startTime.value
        var finish = finishTime.value
        if (start > finish) {
            finish = timeList.size
        }
        val startTime = timeList[start - 1].toString()
        val finishTime = timeList[finish - 1].toString()
        workTimes.add(WorkingDay(day, finishTime, startTime))
    }

    /**
     * this function, gives the NumberPicker sound and vibrate
     */
    private fun checkVibrationIsOn(context: Context) {
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