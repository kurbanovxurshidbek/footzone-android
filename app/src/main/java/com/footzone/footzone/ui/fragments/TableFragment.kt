package com.footzone.footzone.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.footzone.footzone.R
import com.footzone.footzone.adapter.TableViewPagerAdapter
import com.footzone.footzone.databinding.FragmentTableBinding
import com.footzone.footzone.utils.SharedPref
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TableFragment : Fragment() {
    lateinit var sharedPref: SharedPref
    private lateinit var binding: FragmentTableBinding
    private lateinit var tableViewPagerAdapter: TableViewPagerAdapter
    private var isPitchOwner = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTableBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        sharedPref= SharedPref(requireContext())
       val LogIn= sharedPref.getLogIn("LogIn",false)
       if (!LogIn){
           binding.tabelFragmentSignIn.visibility=View.GONE
           binding.tabelFragmentNoSignIn.visibility=View.VISIBLE
       }else{
           binding.tabelFragmentSignIn.visibility=View.VISIBLE
           binding.tabelFragmentNoSignIn.visibility=View.GONE

       }
        tableViewPagerAdapter = TableViewPagerAdapter(requireActivity())
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
            arrayListOf("O'ynaladi", "O'ynalgan")
        )
        binding.tvEnterAccount.setOnClickListener {
            findNavController().navigate(R.id.action_tableFragment_to_signInFragment)
        }


            binding.tabLayoutPitch.setupWithViewPager(
                binding.vpPitchTable,
                arrayListOf("So'rov tushgan", "O'ynalgan")
            )
        }
    }

    private fun addFragmentsToVPOwner() {
        tableViewPagerAdapter.addFragment(BookPitchSentFragment())
        tableViewPagerAdapter.addFragment(PlayedPitchFragment())
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