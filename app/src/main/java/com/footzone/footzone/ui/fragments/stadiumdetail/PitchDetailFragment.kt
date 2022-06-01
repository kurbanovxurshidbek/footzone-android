package com.footzone.footzone.ui.fragments.stadiumdetail

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.footzone.footzone.CalendarDIalog
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentPitchDetailBinding
import com.footzone.footzone.model.Comment
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.utils.Extensions.changeTextBackgroundBlue
import com.footzone.footzone.utils.Extensions.changeTextColorGreen
import com.footzone.footzone.utils.Extensions.changeTextColorRed
import com.footzone.footzone.utils.Extensions.changeTextColorYellow
import com.footzone.footzone.utils.Extensions.hideBottomSheet
import com.footzone.footzone.utils.Extensions.setImageViewBusy
import com.footzone.footzone.utils.Extensions.setImageViewisBusy
import com.footzone.footzone.utils.Extensions.showBottomSheet
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.Integer.parseInt
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class PitchDetailFragment : Fragment() {

    private lateinit var binding: FragmentPitchDetailBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    lateinit var pitch: Pitch
    private lateinit var bottomSheet: View
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    var times: ArrayList<TimeManager> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitch = arguments?.get(PITCH_DETAIL) as Pitch
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_pitch_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPitchDetailBinding.bind(view)
        bottomSheet = view.findViewById(R.id.bottomSheet)
        initViews()
    }

    private fun initViews() {
        allTime()
        //
        refreshAdapter()
        refreshCommentAdapter()
        binding.rbRate.setIsIndicator(true)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.bottomSheet

        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.hideBottomSheet()

        binding.btnOpenBottomSheet.setOnClickListener {
            if (sheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                sheetBehavior.showBottomSheet()
            } else {
                sheetBehavior.hideBottomSheet()
            }
            controlBottomSheetActions()
        }

        binding.cordLayout.setOnClickListener { sheetBehavior.hideBottomSheet() }

        binding.linearNavigation.setOnClickListener {
            requireActivity().shareLocationToGoogleMap(41.33324, 69.21896)
        }
    }

    private fun refreshAdapter() {
        adapter = CustomAdapter(pitch.images)
        binding.recyclerView.adapter = adapter
    }

    private fun refreshCommentAdapter() {
        adapterComment = CommentAdapter(getComments())
        binding.recyclerViewComment.adapter = adapterComment
    }

    private fun getComments(): ArrayList<Comment> {
        val items = ArrayList<Comment>()
        items.add(
            Comment(
                "Jonibek Xolmonov",
                3.5f,
                "18.05.2002",
                "Measure the view and its content to determine the measured width and the measured height. This method is invoked by measure(int, int) and should be overridden by subclasses to provide accurate and efficient measurement of their contents."
            )
        )
        items.add(
            Comment(
                "Odilbek Rustamov",
                2f,
                "11.05.2002",
                "CONTRACT: When overriding this method, you must call setMeasuredDimension(int, int) to store the measured width and height of this view. Failure to do so will trigger an IllegalStateException, thrown by measure(int, int). Calling the superclass' onMeasure(int, int) is a valid use."
            )
        )
        return items
    }

    private fun allTime() {
        times.add(TimeManager("00:00", "01:30", "band"))
        times.add(TimeManager("02:00", "03:30", "band qilinmoqda"))
        times.add(TimeManager("06:00", "07:30", "band"))
        times.add(TimeManager("10:00", "12:30", "band qilinmoqda"))
        times.add(TimeManager("13:00", "14:30", "band"))
        times.add(TimeManager("16:00", "16:30", "band qilinmoqda"))
        times.add(TimeManager("20:00", "20:30", "band"))
        times.add(TimeManager("21:00", "23:30", "band qilinmoqda"))
    }

    /**
     * this function, controls the time it takes to apply to the stadium
     */
    private fun controlBottomSheetActions() {
        val timeList = resources.getStringArray(R.array.timelist)
        var boolStart: Boolean = false
        var boolFinish: Boolean = false

        binding.bottomSheet.ivCalendar.setOnClickListener {
            val dialog = CalendarDIalog{ date ->
                binding.bottomSheet.tvDate.text = date

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.showCalendarDialog(requireActivity())
            }
        }

        binding.bottomSheet.startTime.minValue = 1
        binding.bottomSheet.startTime.maxValue = 47
        binding.bottomSheet.startTime.displayedValues = timeList
        binding.bottomSheet.finishTime.minValue = 1
        binding.bottomSheet.finishTime.maxValue = 47
        binding.bottomSheet.finishTime.displayedValues = timeList

        binding.bottomSheet.startTime.setOnLongPressUpdateInterval(8000)
        binding.bottomSheet.finishTime.setOnLongPressUpdateInterval(8000)


        binding.bottomSheet.startTime.setOnValueChangedListener(NumberPicker.OnValueChangeListener { numberPicker, i, i1 ->

                var inswv  = LocalDate.now().atTime(LocalTime.parse(timeList[i]))
                    .format(DateTimeFormatter.ofPattern("HH:mm"))

                Log.d("TAG", "controlBottomSheetActions: ${inswv}")

                val startTime = "05:30"
                val sts = startTime.split(":");
                val endTime = "15:00"
                val ets = endTime.split(":");

                val stMin = (parseInt(sts[0]) * 60 + parseInt(sts[1]));
                val etMin = (parseInt(ets[0]) * 60 + parseInt(ets[1]));
                if( etMin > stMin) {
                    Toast.makeText(requireContext(), "true", Toast.LENGTH_SHORT).show()
                }

            checkVibrationIsOn(requireContext())

            for (time in times) {
                if (time.startTime == timeList[i]) {
                    boolStart = false
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                    if (time.type.equals("band")) {
                        binding.bottomSheet.ivStartImage.setImageViewBusy()
                        binding.bottomSheet.ivCaution.setImageViewBusy()
                        binding.bottomSheet.tvCaution.text = "Boshqa user tomonidan band qilingan."
                        binding.bottomSheet.tvCaution.changeTextColorRed()
                        break
                    } else {
                        binding.bottomSheet.ivStartImage.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.text =
                            "Boshqa user tomonidan band qilinmoqda!!!"
                        binding.bottomSheet.ivCaution.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.changeTextColorYellow()
                        break
                    }
                } else {
                    boolStart = true
                    binding.bottomSheet.ivStartImage.setImageResource(0)
                    binding.bottomSheet.ivCaution.setImageResource(0)
                    binding.bottomSheet.tvCaution.text = "Bu vaqtda bo'sh joy bor"
                    binding.bottomSheet.tvCaution.changeTextColorGreen()
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                }
            }
        })

        binding.bottomSheet.finishTime.setOnValueChangedListener(NumberPicker.OnValueChangeListener { numberPicker, i, i1 ->

            checkVibrationIsOn(requireContext())

            for (time in times) {
                if (time.startTime == timeList[i]) {
                    boolFinish = false
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                    if (time.type.equals("band")) {
                        binding.bottomSheet.ivFinishImage.setImageViewBusy()
                        binding.bottomSheet.ivCaution.setImageViewBusy()
                        binding.bottomSheet.tvCaution.text = "Boshqa user tomonidan band qilingan."
                        binding.bottomSheet.tvCaution.changeTextColorRed()
                        break
                    } else {
                        binding.bottomSheet.ivFinishImage.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.text =
                            "Boshqa user tomonidan band qilinmoqda!!!"
                        binding.bottomSheet.ivCaution.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.changeTextColorYellow()
                        break
                    }
                } else {
                    boolFinish = true
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                    binding.bottomSheet.ivFinishImage.setImageResource(0)
                    binding.bottomSheet.ivCaution.setImageResource(0)
                    binding.bottomSheet.tvCaution.text = "Bu vaqtda bo'sh joy bor"
                    binding.bottomSheet.tvCaution.changeTextColorGreen()
                }
            }
        })

        binding.bottomSheet.tvCancel.setOnClickListener { sheetBehavior.hideBottomSheet() }

        sheetBehavior.addBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    binding.frameWrapper.setBackgroundColor(Color.parseColor("#40000000"))
                }
                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    binding.frameWrapper.setBackgroundColor(Color.TRANSPARENT)
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })
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