package com.footzone.footzone.ui.fragments.table

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.footzone.footzone.R
import com.footzone.footzone.adapter.TableViewPagerAdapter
import com.footzone.footzone.databinding.FragmentTableBinding
import com.footzone.footzone.model.playhistory.Data
import com.footzone.footzone.model.playhistory.PlayHistoryResponse
import com.footzone.footzone.ui.fragments.BaseFragment
import com.footzone.footzone.ui.fragments.bookpitchsent.BookPitchSentFragment
import com.footzone.footzone.ui.fragments.played.PlayedPitchFragment
import com.footzone.footzone.ui.fragments.playing.PlayingPitchFragment
import com.footzone.footzone.ui.fragments.stadiumhistory.StadiumGameHistoryFragment
import com.footzone.footzone.utils.KeyValues.IS_OWNER
import com.footzone.footzone.utils.KeyValues.LOG_IN
import com.footzone.footzone.utils.SharedPref
import com.footzone.footzone.utils.UiStateObject
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TableFragment : BaseFragment(R.layout.fragment_table) {

    @Inject
    lateinit var sharedPref: SharedPref
    private lateinit var binding: FragmentTableBinding
    private lateinit var tableViewPagerAdapter: TableViewPagerAdapter
    private var isPitchOwner = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTableBinding.bind(view)
        isPitchOwner = sharedPref.getIsOwner(IS_OWNER)
        initViews()
    }

    private fun initViews() {
        val logIn = sharedPref.getLogIn(LOG_IN, false)
        if (!logIn) {
            binding.tabelFragmentSignIn.visibility = View.GONE
            binding.tabelFragmentNoSignIn.visibility = View.VISIBLE

            binding.tvEnterAccount.setOnClickListener {
                findNavController().navigate(R.id.action_tableFragment_to_signInFragment)
            }
        } else {
            binding.tabelFragmentSignIn.visibility = View.VISIBLE
            binding.tabelFragmentNoSignIn.visibility = View.GONE

            tableViewPagerAdapter = TableViewPagerAdapter(requireActivity())

            controlViewPagerState()
        }
    }

    private fun controlViewPagerState() {
        if (!isPitchOwner) {
            addFragmentsToVPUser()
            binding.vpPitchTable.adapter = tableViewPagerAdapter

            binding.tabLayoutPitch.setupWithViewPager(
                binding.vpPitchTable,
                arrayListOf("O'ynaladi", "O'ynalgan")
            )
        } else {
            addFragmentsToVPOwner()
            binding.vpPitchTable.adapter = tableViewPagerAdapter

            binding.tabLayoutPitch.setupWithViewPager(
                binding.vpPitchTable,
                arrayListOf("So'rov tushgan", "O'ynalgan")
            )
        }
    }

    private fun addFragmentsToVPOwner() {
        tableViewPagerAdapter.addFragment(BookPitchSentFragment())
        tableViewPagerAdapter.addFragment(StadiumGameHistoryFragment())
    }

    private fun addFragmentsToVPUser() {
        tableViewPagerAdapter.addFragment(PlayingPitchFragment())
        tableViewPagerAdapter.addFragment(PlayedPitchFragment())
    }

    private fun TabLayout.setupWithViewPager(viewPager: ViewPager2, labels: List<String>) {
        if (labels.size != viewPager.adapter?.itemCount)
            throw Exception("Item count is not equal labels size")

        TabLayoutMediator(this, viewPager) { tab, position ->
            tab.text = labels[position]
        }.attach()
    }
}