package com.secureappinc.musicplayer.ui.genres.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.utils.dpToPixel
import com.secureappinc.musicplayer.utils.gone
import kotlinx.android.synthetic.main.fragment_genres.*

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_genres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.setTitle(R.string.genres)

        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(com.secureappinc.musicplayer.R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = false

        val rltContainer = activity?.findViewById<RelativeLayout>(com.secureappinc.musicplayer.R.id.rltContainer)
        rltContainer?.gone()

        val viewModel = ViewModelProviders.of(this).get(GenresViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = gridLayoutManager
        val spacingDp = requireActivity().dpToPixel(8f)
        val marginDp = requireActivity().dpToPixel(8f)
        recyclerView.addItemDecoration(
            GenresItemSpacing(
                3,
                spacingDp,
                true,
                0
            )
        )

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0) {
                    return 3
                } else if (position == 1) {
                    return 2
                } else {
                    return 1
                }
            }
        }

        viewModel.genres.observe(this, Observer { genres ->
            recyclerView.adapter = GenresAdapter(genres)
        })

        viewModel.loadAllGenres()
    }
}