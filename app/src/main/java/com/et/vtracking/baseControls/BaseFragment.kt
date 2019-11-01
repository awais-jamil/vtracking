package com.et.vtracking.baseControls


import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.et.vtracking.support.ProgressLoader

/**
 * A simple [Fragment] subclass.
 */
open class BaseFragment : Fragment() {

    private var progressLoader: ProgressLoader? = null
    var interruptTouchEvents = true

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    fun setActionBarTitle(title: String) {

        if(activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = title
        }
    }

    fun showPrompt(context: Context, title: String, message: String) {

        showPrompt(context, title, message, null)
    }

    fun showPrompt(context: Context, title: String, message: String, clickListener: DialogInterface.OnClickListener?) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(android.R.string.yes, clickListener)

        builder.show()
    }

    protected fun displayLoadingIndicator(message: String) {
        hideLoadingIndicator()

        val fm = activity?.supportFragmentManager

        fm?.let {

            progressLoader = ProgressLoader.newInstance(message)

            progressLoader?.show(it, ProgressLoader::class.java.name)
        }

    }

    protected fun hideLoadingIndicator() {

        progressLoader?.let {

            it.dialog?.let {dialog ->

                if(dialog.isShowing)
                    it.dismiss()
            }
        }
    }
}
