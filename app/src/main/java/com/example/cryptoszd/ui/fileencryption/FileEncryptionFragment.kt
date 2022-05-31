package com.example.cryptoszd.ui.fileencryption

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import com.example.cryptoszd.R
import com.example.cryptoszd.utils.FileUtils
import com.example.cryptoszd.utils.SingleToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wang.avi.AVLoadingIndicatorView
import java.io.File

class FileEncryptionFragment: RainbowCakeFragment<FileEncryptionViewState, FileEncryptionViewModel>() {
    private lateinit var encryptionModeTV: AutoCompleteTextView
    private lateinit var chooseFileButton: MaterialButton
    private lateinit var filePathTV: TextView
    private lateinit var filePasswordTIL: TextInputLayout
    private lateinit var filePasswordTIET: TextInputEditText
    private lateinit var fileConfirmPasswordTIL: TextInputLayout
    private lateinit var fileConfirmPasswordTIET: TextInputEditText
    private lateinit var chooseDirectoryButton: MaterialButton
    private lateinit var chosenDirectoryTV: TextView
    private lateinit var encryptionButton: MaterialButton
    private lateinit var encryptionLoading: AVLoadingIndicatorView

    private lateinit var browseFileResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var browseDirectoryResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:  ActivityResultLauncher<String>
    private lateinit var chosenFile: File
    private lateinit var chosenDirectory: String


    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_fileencryption

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        requestNeededPermission()
    }

    private fun init() {
        encryptionModeTV = requireView().findViewById(R.id.encryptionModeTV)
        chooseFileButton = requireView().findViewById(R.id.chooseFileButton)
        filePathTV = requireView().findViewById(R.id.filePathTV)
        filePasswordTIL = requireView().findViewById(R.id.filePasswordTIL)
        filePasswordTIET = requireView().findViewById(R.id.filePasswordTIET)
        fileConfirmPasswordTIL = requireView().findViewById(R.id.fileConfirmPasswordTIL)
        fileConfirmPasswordTIET = requireView().findViewById(R.id.fileConfirmPasswordTIET)
        chooseDirectoryButton = requireView().findViewById(R.id.chooseDirectoryButton)
        chosenDirectoryTV = requireView().findViewById(R.id.chosenDirectoryTV)
        chosenDirectoryTV.text = getString(R.string.chosen_directory, "")
        encryptionButton = requireView().findViewById(R.id.encryptionButton)
        encryptionLoading = requireView().findViewById(R.id.encryptionLoading)

        browseFileResultLauncher()
        browseDirectoryResultLauncher()
        permissionLauncher()

        val items = listOf(getString(R.string.encrypt), getString(R.string.decrypt))
        val adapter = ArrayAdapter(requireContext(), R.layout.encryption_list_item, items)
        encryptionModeTV.setAdapter(adapter)
        encryptionModeTV.setOnItemClickListener { _, _, _, id ->
            when(id.toInt()) {
                0 -> {
                    encryptionButton.text = getString(R.string.encrypt)
                    fileConfirmPasswordTIL.visibility = View.VISIBLE

                }
                1 -> {
                    encryptionButton.text = getString(R.string.decrypt)
                    fileConfirmPasswordTIL.visibility = View.GONE
                }
                else -> {
                    encryptionButton.text = getString(R.string.encrypt)
                    fileConfirmPasswordTIL.visibility = View.VISIBLE
                }
            }
        }

        chooseFileButton.setOnClickListener {
            browseFile()
        }
        chooseDirectoryButton.setOnClickListener {
            browseDirectory()
        }

        filePasswordTIL.editText?.addTextChangedListener {
            filePasswordTIL.error = ""
        }
        fileConfirmPasswordTIL.editText?.addTextChangedListener {
            fileConfirmPasswordTIL.error = ""
        }
        encryptionButton.setOnClickListener {
            if(checkEditTexts()) {
                val password = filePasswordTIL.editText?.text.toString().toCharArray()
                val outputFile: File
                when(encryptionModeTV.text.toString()) {
                    getString(R.string.encrypt) -> {
                        outputFile = File(chosenDirectory + "/" + chosenFile.name + ".enc")
                        viewModel.encrypt(password, chosenFile, outputFile)
                    }
                    getString(R.string.decrypt) -> {
                        outputFile = File(chosenDirectory + "/" + chosenFile.nameWithoutExtension)
                        viewModel.decrypt(password, chosenFile, outputFile)
                    }
                    else -> {
                        outputFile = File(chosenDirectory + "/" + chosenFile.name + ".enc")
                        viewModel.encrypt(password, chosenFile, outputFile)
                    }
                }
            }
        }
    }

    private fun checkEditTexts(): Boolean {
        if(filePasswordTIL.editText?.text.toString().isEmpty()) {
            filePasswordTIL.error = getString(R.string.empty_password_error)
            filePasswordTIL.requestFocus()
            return false
        }
        if(filePasswordTIL.editText?.text.toString().length < 6) {
            filePasswordTIL.error = getString(R.string.password_min_6)
            filePasswordTIL.requestFocus()
            return false
        }
        if(filePasswordTIL.editText?.text.toString() != fileConfirmPasswordTIL.editText?.text.toString() && encryptionModeTV.text.toString() !=  getString(R.string.decrypt)) {
            fileConfirmPasswordTIL.error = getString(R.string.passwords_dont_match)
            fileConfirmPasswordTIL.requestFocus()
            return false
        }
        return true
    }

    private fun permissionLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                chooseFileButton.isEnabled = true
            }
            else {
                SingleToast.show(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_LONG)
            }
        }
    }

    private fun browseFileResultLauncher() {
        browseFileResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val fileUri: Uri = data.data!!
                    val filePath: String
                    try {
                        filePath = FileUtils.getPath(requireContext(), fileUri) as String
                    } catch (e: Exception) {
                        SingleToast.show(requireContext(), "Error: $e", Toast.LENGTH_SHORT)
                        return@registerForActivityResult
                    }
                    chosenFile = File(filePath)
                    filePathTV.text = getString(R.string.file, chosenFile.name)
                    chosenDirectory = chosenFile.parent!!
                    chosenDirectoryTV.text = getString(R.string.chosen_directory, chosenFile.parent)
                    chooseDirectoryButton.isEnabled = true
                    encryptionButton.isEnabled = true
                }

            } else if(result.resultCode == Activity.RESULT_CANCELED) {
                SingleToast.show(requireContext(), getString(R.string.no_file_was_chosen), Toast.LENGTH_SHORT)
            } else {
                SingleToast.show(requireContext(), getString(R.string.something_went_wrong_only), Toast.LENGTH_SHORT)
            }
        }
    }

    private fun browseFile() {
        var chooseFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        chooseFileIntent.type = "*/*"
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFileIntent = Intent.createChooser(chooseFileIntent, getString(R.string.choose_a_file))
        browseFileResultLauncher.launch(chooseFileIntent)
    }

    private fun browseDirectoryResultLauncher() {
        browseDirectoryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val fileUri: Uri = data.data!!
                    val docUri = DocumentsContract.buildDocumentUriUsingTree(fileUri, DocumentsContract.getTreeDocumentId(fileUri))
                    try {
                        chosenDirectory = FileUtils.getPath(requireContext(), docUri) as String
                        chosenDirectoryTV.text = getString(R.string.chosen_directory, chosenDirectory)
                    } catch (e: Exception) {
                        SingleToast.show(requireContext(), "Error: $e", Toast.LENGTH_SHORT)
                    }
                }

            } else {
                SingleToast.show(requireContext(),  getString(R.string.something_went_wrong_only), Toast.LENGTH_SHORT)
            }
        }
    }

    private fun browseDirectory() {
        val directoryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        directoryIntent.addCategory(Intent.CATEGORY_DEFAULT)
        directoryIntent.putExtra("android.content.extra.SHOW_ADVANCED", true)
        directoryIntent.putExtra("android.content.extra.FANCY", true)
        directoryIntent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        browseDirectoryResultLauncher.launch(directoryIntent)
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            chooseFileButton.isEnabled = false
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun render(viewState: FileEncryptionViewState) {
        when(viewState) {
            Default -> {
                encryptionLoading.smoothToHide()
            }
            is Error -> {
                when (viewState.exception.message) {
                    "Wrong Password" -> {
                        SingleToast.show(requireContext(),  getString(R.string.wrong_password), Toast.LENGTH_SHORT)
                    }
                    else -> {
                        SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_SHORT)
                    }
                }
                encryptionLoading.smoothToHide()
            }
            Loading -> {
                encryptionLoading.smoothToShow()
            }
            Success -> {
                SingleToast.show(requireContext(),  getString(R.string.success), Toast.LENGTH_SHORT)
                encryptionLoading.smoothToHide()
            }
        }.exhaustive
    }
}