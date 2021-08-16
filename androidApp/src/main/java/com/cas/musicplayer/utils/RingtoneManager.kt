package com.cas.musicplayer.utils

import android.app.RecoverableSecurityException
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.cas.musicplayer.R
import com.mousiki.shared.domain.models.Song

class RingtoneManager(val context: Context) {

    fun setRingtone(song: Song): Boolean {
        val resolver = context.contentResolver
        val uri = song.getFileUri()
        try {
            val values = ContentValues(2)
            values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1")
            values.put(MediaStore.Audio.AudioColumns.IS_ALARM, "1")
            resolver.update(uri, values, null, null)
        } catch (exception: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val rsException = exception as? RecoverableSecurityException ?: return false
                val intentSender = rsException.userAction.actionIntent.intentSender ?: return false
                val activity = context as? AppCompatActivity
                activity?.startIntentSenderForResult(intentSender, 10, null, 0, 0, 0, null)
            }
            return false
        }

        try {
            val cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.MediaColumns.TITLE),
                BaseColumns._ID + "=?",
                arrayOf(song.id.toString()), null
            )
            cursor.use { cursorSong ->
                if (cursorSong != null && cursorSong.count == 1) {
                    cursorSong.moveToFirst()
                    Settings.System.putString(resolver, Settings.System.RINGTONE, uri.toString())
                    val message = context
                        .getString(R.string.x_has_been_set_as_ringtone, cursorSong.getString(0))
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
            return true
        } catch (ignored: SecurityException) {
        }
        return false
    }

    companion object {

        fun requiresDialog(context: Context): Boolean {
            return !SystemSettings.canWriteSettings(context)
        }

        fun showDialog(context: Context) {
            MaterialDialog(context).show {
                message(R.string.dialog_message_set_ringtone)
                title(R.string.dialog_title_set_ringtone)
                positiveButton(R.string.ok) {
                    SystemSettings.enableSettingModification(context)
                }
                negativeButton(R.string.cancel)
                getActionButton(WhichButton.NEGATIVE).updateTextColor(Color.parseColor("#808184"))
            }
        }
    }
}