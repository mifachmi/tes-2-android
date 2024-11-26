package id.mifachmi.tesdua.ui.inputuangmasuk

import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.mifachmi.tesdua.R
import id.mifachmi.tesdua.data.local.entity.IncomeEntity
import id.mifachmi.tesdua.databinding.FragmentInputUangMasukBinding
import id.mifachmi.tesdua.ui.inputuangmasuk.sheet.IncomeTypeBottomSheet
import id.mifachmi.tesdua.utils.ViewModelFactory
import id.mifachmi.tesdua.utils.getCurrentTime
import id.mifachmi.tesdua.utils.getImageUri
import java.util.Date

class InputUangMasukFragment(val isEdit: Boolean) : Fragment() {

    private var _binding: FragmentInputUangMasukBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<InputUangMasukViewModel> {
        context?.let { ctx -> ViewModelFactory.getInstance(ctx) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInputUangMasukBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateFieldsFromArgs()
        setupToolbar()
        setupFormData()
        handleIncomeType()
        handleUploadPhoto()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationIcon(R.drawable.baseline_arrow_back_24)
            setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        binding.btnSave.setOnClickListener {
            saveData()
        }
    }

    private fun setupFormData() {
        binding.apply {
            etToWho.doOnTextChanged { text, _, _, _ -> validateEditText(etToWho, text) }
            etFromWho.doOnTextChanged { text, _, _, _ -> validateEditText(etFromWho, text) }
            etAmount.doOnTextChanged { text, _, _, _ -> validateEditText(etAmount, text) }
            etNotes.doOnTextChanged { text, _, _, _ -> validateEditText(etNotes, text) }
        }
    }

    private fun validateEditText(editText: TextView, text: CharSequence?) {
        editText.error =
            if (text.isNullOrEmpty()) getString(R.string.must_be_filled) else null
    }

    private fun handleIncomeType() {
        binding.etTypeIncome.setOnClickListener {
            showIncomeTypeDialog()
        }

        binding.tvMoreInfo.setOnClickListener {
            showIncomeTypeInformation()
        }
    }

    private fun showIncomeTypeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_income_type, null)
        val pendapatanLainOption = dialogView.findViewById<TextView>(R.id.tvOtherIncome)
        val nonPendapatanOption = dialogView.findViewById<TextView>(R.id.tvNonIncome)

        context?.let { ctx ->
            val mainDialog = MaterialAlertDialogBuilder(ctx)
                .setView(dialogView)
                .show()

            pendapatanLainOption.setOnClickListener {
                binding.etTypeIncome.setText(pendapatanLainOption.text)
                mainDialog.dismiss()
            }

            nonPendapatanOption.setOnClickListener {
                binding.etTypeIncome.setText(nonPendapatanOption.text)
                mainDialog.dismiss()
            }

            mainDialog.show()
        }
    }

    private fun showIncomeTypeInformation() {
        val deviceOrientation = resources.configuration.orientation
        if (deviceOrientation == ORIENTATION_PORTRAIT) {
            val incomeTypeInfoBottomSheet = IncomeTypeBottomSheet()
            incomeTypeInfoBottomSheet.show(parentFragmentManager, incomeTypeInfoBottomSheet.tag)
        } else {
            showIncomeTypeInformationLandscape()
        }
    }

    private fun showIncomeTypeInformationLandscape() {
        val dialogView = layoutInflater.inflate(R.layout.sheet_income_type_info, null)
        context?.let { ctx ->
            val mainDialog = MaterialAlertDialogBuilder(ctx)
                .setView(dialogView)
                .show()
            mainDialog.show()
        }
    }

    private fun handleUploadPhoto() {
        binding.flPhoto.setOnClickListener {
            if (isEdit) {
                binding.tvDummyFoto.visibility = View.GONE
                showImageDialog(currentImageUri.toString())
            } else {
                binding.tvDummyFoto.visibility = View.VISIBLE
                showImageSourceDialog()
            }
        }
    }

    private fun showImageSourceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_upload_photo, null)
        val cameraOption = dialogView.findViewById<LinearLayoutCompat>(R.id.llCamera)
        val galleryOption = dialogView.findViewById<LinearLayoutCompat>(R.id.llGallery)

        context?.let { ctx ->
            val mainDialog = MaterialAlertDialogBuilder(ctx)
                .setView(dialogView)
                .show()

            cameraOption.setOnClickListener {
                openCamera()
                mainDialog.dismiss()
            }

            galleryOption.setOnClickListener {
                openGallery()
                mainDialog.dismiss()
            }

            mainDialog.show()
        }
    }

    private fun openCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        context?.let { ctx ->
            if (uri != null) {
                currentImageUri = uri
                currentImageUri?.let { currentUri ->
                    ctx.contentResolver.releasePersistableUriPermission(
                        currentUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                showImage()
            } else
                Toast.makeText(
                    ctx,
                    getString(R.string.image_not_avaliable), Toast.LENGTH_SHORT
                )
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        println("currentImageUri: $currentImageUri")
        currentImageUri.let {
            binding.ivPhoto.setImageURI(it)
        }

        setupEditAndDeletePhoto()


    }

    private fun showImageDialog(imageUri: String) {
        println("imageUri: $imageUri")
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image, null)
        val ivPhoto = dialogView.findViewById<ImageView>(R.id.ivImage)
        val resolver = requireContext().contentResolver
        val uri = Uri.parse(imageUri)

        resolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        ivPhoto.setImageBitmap(bitmap)

        dialog.show()
    }

    private fun setupEditAndDeletePhoto() {
        if (currentImageUri != null) {
            binding.apply {
                flPhoto.setOnClickListener {
                    return@setOnClickListener
                }

                llAction.visibility = View.VISIBLE
                btnChangeImage.setOnClickListener {
                    showImageSourceDialog()
                }

                btnDeleteImage.setOnClickListener {
                    currentImageUri = null
                    ivPhoto.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.color.grey_EC
                        )
                    )
                }
            }
        }
    }

    private fun saveData(): Boolean {
        val from = binding.etFromWho.text.toString()
        val to = binding.etToWho.text.toString()
        val amount = binding.etAmount.text.toString()
        val note = binding.etNotes.text.toString()
        val incomeType = binding.etTypeIncome.text.toString()
        val currentDate = Date()

        return if (from.isNotEmpty() && to.isNotEmpty() && amount.isNotEmpty() && note.isNotEmpty() && incomeType.isNotEmpty()) {
            try {
                val txtToast: String
                if (isEdit) {
                    val id = arguments?.getInt("id", -1) ?: -1
                    val updatedData = IncomeEntity(
                        id = id,
                        time = getCurrentTime(),
                        to = to,
                        type = incomeType,
                        date = currentDate.time,
                        description = note,
                        amount = amount.toInt(),
                        from = from,
                        imageUri = currentImageUri.toString(),
                    )
                    viewModel.updateData(updatedData)
                    txtToast = "Updated"
                } else {
                    viewModel.insertData(
                        time = getCurrentTime(),
                        to = to,
                        from = from,
                        amount = amount.toInt(),
                        description = note,
                        type = incomeType,
                        date = currentDate.time,
                        imageUri = currentImageUri.toString()
                    )
                    txtToast = "Saved"
                }
                Toast.makeText(requireContext(), txtToast, Toast.LENGTH_SHORT)
                    .show()
                parentFragmentManager.popBackStack()
            } catch (e: NumberFormatException) {
                Toast.makeText(
                    requireContext(),
                    "Invalid Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        } else {
            Toast.makeText(requireContext(), R.string.must_be_filled, Toast.LENGTH_SHORT)
                .show()
            false
        }
    }

    private fun populateFieldsFromArgs() {
        if (isEdit) {
            val args = arguments
            val id = args?.getInt("id", -1) ?: -1

            if (id != -1) {
                val to = args?.getString("to", "") ?: ""
                val from = args?.getString("from", "") ?: ""
                val description = args?.getString("description", "") ?: ""
                val amount = args?.getInt("amount", 0) ?: 0
                val type = args?.getString("type", "") ?: ""
                currentImageUri = Uri.parse(args?.getString("imageUri", ""))

                binding.etToWho.setText(to)
                binding.etFromWho.setText(from)
                binding.etNotes.setText(description)
                binding.etAmount.setText(amount.toString())
                binding.etTypeIncome.setText(type)

                showImage()
            }
        }
    }

    private fun zoomImage() {
        binding.ivPhoto.setOnClickListener {
            // TODO: Implement zoom image
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}