package uz.authentication.imagetranslate

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import uz.authentication.helper.NetworkHelper
import uz.authentication.imagetranslate.databinding.ActivityMainBinding
import uz.authentication.utils.LanguageShared


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var engRus: Translator
    lateinit var rusEng: Translator
    lateinit var networkHelper: NetworkHelper
    var language = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        downloadResource()
        checkLanguage()

        binding.menuImage.setOnClickListener {

        }

        binding.fromEditText.addTextChangedListener {
            translate(language, binding.fromEditText.text.toString())
        }
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