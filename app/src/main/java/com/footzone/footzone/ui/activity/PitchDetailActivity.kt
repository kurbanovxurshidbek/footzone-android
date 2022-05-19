package com.footzone.footzone.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.ActivityPitchDetailBinding
import com.footzone.footzone.model.Comment
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import java.io.Serializable

class PitchDetailActivity : AppCompatActivity() {
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    lateinit var binding:ActivityPitchDetailBinding
    private lateinit var pitch: Pitch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPitchDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        pitch = intent.getSerializableExtra(PITCH_DETAIL) as Pitch
        refreshAdapter()
        refreshCommentAdapter()
    }

    private fun refreshAdapter() {
        adapter = CustomAdapter(pitch.images)
        binding.recyclerView.adapter = adapter
    }
    private fun refreshCommentAdapter() {
        adapterComment = CommentAdapter(getComments())
        binding.recyclerViewComment.adapter = adapterComment
    }

    fun getComments(): ArrayList<Comment>{
        val items = ArrayList<Comment>()
        items.add(Comment("Jonibek Xolmonov", 3.5f,"18.05.2002","Measure the view and its content to determine the measured width and the measured height. This method is invoked by measure(int, int) and should be overridden by subclasses to provide accurate and efficient measurement of their contents."))
        items.add(Comment("Odilbek Rustamov", 2f,"11.05.2002","CONTRACT: When overriding this method, you must call setMeasuredDimension(int, int) to store the measured width and height of this view. Failure to do so will trigger an IllegalStateException, thrown by measure(int, int). Calling the superclass' onMeasure(int, int) is a valid use."))
        return items
    }
}