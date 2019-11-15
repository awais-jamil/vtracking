package com.et.tracker.support

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.et.tracker.R
import kotlinx.android.synthetic.main.cs_progress_loader.view.*

class ProgressLoader: DialogFragment() {

    fun ProgressLoader() {

    }

    companion object {

        @JvmStatic fun newInstance(message: String): ProgressLoader {

            val progressLoader = ProgressLoader()

            val args = Bundle()
            args.putString("message", message)

            progressLoader.arguments = args

            return progressLoader
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.cs_progress_loader, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        isCancelable = false

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = arguments?.getString("message")

        view.message_text_view.text = message

    }
}