package uz.authentication.imagetranslate

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.navigation.NavigationView
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import uz.authentication.helper.NetworkHelper
import uz.authentication.imagetranslate.databinding.AboutDialogBinding
import uz.authentication.imagetranslate.databinding.ActivityMainBinding
import uz.authentication.imagetranslate.databinding.FeedbackDialogBinding
import uz.authentication.utils.LanguageShared
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var engRus: Translator
    lateinit var rusEng: Translator
    lateinit var networkHelper: NetworkHelper
    var language = 0
    private lateinit var mTTsFrom: TextToSpeech
    private lateinit var mTTsTrans: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        downloadResource()
        checkLanguage()
        clickPlaySound()
        replaceLanguage()
        deleteText()
        copyTextOnClick()
        navigationViewMenu()
        binding.fromEditText.addTextChangedListener {
            translate(language, binding.fromEditText.text.toString())
        }


    }
    private fun feedbackDialog() {
        var dialog = AlertDialog.Builder(this).create()
        var item = FeedbackDialogBinding.inflate(layoutInflater)
        dialog.setView(item.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        item.cardTelegram.setOnClickListener {
            intentLink("https://t.me/shokirov_ollohberdi")
        }
        item.cardInstagram.setOnClickListener {
            intentLink("https://www.instagram.com/shokirovollohberdi/")
        }
        item.cardPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "+998916661180")
            startActivity(dialIntent)
        }
    }

    private fun aboutDialog() {
        var dialog = AlertDialog.Builder(this).create()
        var item = AboutDialogBinding.inflate(layoutInflater)
        dialog.setView(item.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }

    private fun intentLink(url: String) {
        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(myIntent)
    }

    private fun navigationViewMenu() {
        binding.menuImage.setOnClickListener {
            binding.drawableRoot.open()
            binding.navigationView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {

                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.menuAbout -> {
                            aboutDialog()
                        }
                        R.id.menuContactme -> {
                            feedbackDialog()
                        }
                    }
                    return true
                }
            })
        }
    }

    private fun copyTextOnClick() {
        binding.copyTheTranslatetext.setOnClickListener {
            copyText(binding.translateEditText.text.toString())
        }
        binding.copyTheoriginalText.setOnClickListener {
            copyText(binding.fromEditText.text.toString())
        }
    }

    @SuppressLint("ServiceCast")
    private fun copyText(copyText: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = copyText
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        } else {
            val clipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", copyText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteText() {
        binding.clearOriginal.setOnClickListener {
            binding.fromEditText.text.clear()
        }
        binding.clearTrans.setOnClickListener {
            binding.translateEditText.text.clear()
        }
    }

    private fun replaceLanguage() {
        binding.cardTranslateReplace.setOnClickListener {
            binding.fromEditText.text.clear()
            binding.translateEditText.text.clear()
            when (language) {
                0 -> {
                    language = 1
                    LanguageShared.init(this)
                    LanguageShared.language = 1
                }
                1 -> {
                    language = 0
                    LanguageShared.init(this)
                    LanguageShared.language = 0
                }
            }
            checkLanguage()
        }
    }

    private fun clickPlaySound() {
        binding.soundPlayFrom.setOnClickListener {
            speakOriginal(language, binding.fromEditText.text.toString())
        }
        binding.soundPlayTranslate.setOnClickListener {
            speakTranslate(language, binding.translateEditText.text.toString())
        }
    }

    private fun speakTranslate(language: Int, text: String) {
        when (language) {
            1 -> {
                mTTsFrom = TextToSpeech(this) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val result = mTTsFrom.setLanguage(Locale.ENGLISH)
                        var pitch = 0.8f
                        var speed = 1.1f
                        mTTsFrom.setPitch(pitch)
                        mTTsFrom.setSpeechRate(speed)
                        mTTsFrom.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                    }
                }
            }
            0 -> {
                Toast.makeText(this, "Application not support Russian Language", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun speakOriginal(language: Int, text: String) {
        when (language) {
            0 -> {
                mTTsFrom = TextToSpeech(this) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val result = mTTsFrom.setLanguage(Locale.ENGLISH)
                        var pitch = 0.8f
                        var speed = 1.1f
                        mTTsFrom.setPitch(pitch)
                        mTTsFrom.setSpeechRate(speed)
                        mTTsFrom.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                    }
                }
            }
            1 -> {
                Toast.makeText(this, "Application not support Russian Language", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkLanguage() {
        LanguageShared.init(this)
        language = LanguageShared.language

        when (language) {
            0 -> {
                binding.fromEditText.setHint(R.string.Text)
                binding.translateEditText.setHint(R.string.Translate)
            }
            1 -> {
                binding.fromEditText.setHint(R.string.TextRussian)
                binding.translateEditText.setHint(R.string.TranslateRussian)
            }
        }
    }

    private fun translate(i: Int, text: String) {
        when (i) {
            0 -> {
                engRus.translate(text).addOnSuccessListener {
                    binding.translateEditText.setText(it)
                }
            }
            1 -> {
                rusEng.translate(text).addOnSuccessListener {
                    binding.translateEditText.setText(it)
                }
            }
        }

    }


    private fun downloadResource() {
        val option1 = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.RUSSIAN)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
        rusEng = Translation.getClient(option1)
        val option2 = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.RUSSIAN)
                .build()
        engRus = Translation.getClient(option2)
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Downloading resources"
        pDialog.setCancelable(false)
        pDialog.show()
        engRus.downloadModelIfNeeded().addOnSuccessListener {
            rusEng.downloadModelIfNeeded().addOnSuccessListener {
                pDialog.cancel()
            }
        }
    }


}