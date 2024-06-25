package id.project.stories.utils.component

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import id.project.stories.databinding.CustomAlertDialogBinding

class CustomAlertDialog(private val context: Context) {
    private lateinit var binding: CustomAlertDialogBinding
    private lateinit var alertDialog: AlertDialog

    fun create(
        title: String,
        message: String,
        hasNegativeBtn: Boolean,
        onPositiveButtonClick: () -> Unit,
        onNegativeButtonClick: () -> Unit
    ) {
        binding = CustomAlertDialogBinding.inflate(LayoutInflater.from(context))

        binding.negativeButton.visibility = if (hasNegativeBtn) View.VISIBLE else View.GONE

        // Set Alert Dialog Component :
        binding.apply {
            this.title.text = title
            this.message.text = message
            positiveButton.setOnClickListener {
                onPositiveButtonClick()
                alertDialog.dismiss()
            }
            negativeButton.setOnClickListener {
                onNegativeButtonClick()
                alertDialog.dismiss()
            }
        }

        // Set AlertDialog Builder :
        alertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        alertDialog.show()
    }
}