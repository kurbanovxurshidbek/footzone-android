package com.footzone.footzone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PitchBookSentAdapter
import com.footzone.footzone.databinding.FragmentBookPitchSentBinding
import com.footzone.footzone.databinding.LayoutAcceptBinding
import com.footzone.footzone.databinding.LayoutDeclineBinding
import com.footzone.footzone.model.Hour
import com.footzone.footzone.model.PitchHistory
import com.footzone.footzone.utils.AcceptDeclineDialog
import java.util.ArrayList

class BookPitchSentFragment : Fragment() {

    private lateinit var binding: FragmentBookPitchSentBinding
    private lateinit var playingPitchAdapter: PitchBookSentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_pitch_sent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBookPitchSentBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        playingPitchAdapter = PitchBookSentAdapter { toAccept ->
            manageAcceptDeclineClick(toAccept)
        }
        playingPitchAdapter.submitData(getPlayedPitchHistory())
        refreshAdapter()
    }

    private fun manageAcceptDeclineClick(toAccept: Boolean) {
        if (toAccept) {
            val acceptDeclineDialog =
                AcceptDeclineDialog(requireContext()).instance(
                    LayoutAcceptBinding.inflate(
                        LayoutInflater.from(requireContext())
                    ).root
                )
            acceptDeclineDialog.show()
        } else {
            val acceptDeclineDialog =
                AcceptDeclineDialog(requireContext()).instance(
                    LayoutDeclineBinding.inflate(
                        LayoutInflater.from(requireContext())
                    ).root
                )
            acceptDeclineDialog.show()
        }
    }

    private fun refreshAdapter() {
        binding.rvPlayingPitches.adapter = playingPitchAdapter
    }

    private fun getPlayedPitchHistory(): ArrayList<PitchHistory> {
        return ArrayList<PitchHistory>().apply {
            for (i in 0..5) {
                this.add(PitchHistory("Acme", "29-may, chorshanba", Hour("16:00", "18:00"), 100000))
            }
        }
    }
}