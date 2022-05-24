package com.footzone.footzone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.footzone.footzone.R
import com.footzone.footzone.adapter.PlayedPitchAdapter
import com.footzone.footzone.databinding.FragmentPlayedPitchBinding
import com.footzone.footzone.model.Hour
import com.footzone.footzone.model.PitchHistory
import java.util.ArrayList

class PlayedPitchFragment : Fragment() {

    private lateinit var binding: FragmentPlayedPitchBinding
    private lateinit var playedPitchAdapter: PlayedPitchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_played_pitch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlayedPitchBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        playedPitchAdapter = PlayedPitchAdapter()
        playedPitchAdapter.submitData(getPlayedPitchHistory())
        refreshAdapter()
    }

    private fun refreshAdapter() {
        binding.rvPlayedPitches.adapter = playedPitchAdapter
    }

    private fun getPlayedPitchHistory(): ArrayList<PitchHistory> {
        return ArrayList<PitchHistory>().apply {
            for (i in 0..5) {
                this.add(PitchHistory("Acme", "29-may, chorshanba", Hour("16:00", "18:00"), 100000))
            }
        }
    }
}