package org.d3if0055.assessment2.ui.screen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.d3if0055.assessment2.R
import org.d3if0055.assessment2.database.ResepDb
import org.d3if0055.assessment2.ui.theme.Assessment2Theme
import org.d3if0055.assessment2.util.ViewModelFactory

const val KEY_ID_RESEP = "idResep"

@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, id: Long? = null) {
    val context = LocalContext.current
    val db = ResepDb.getInstance(context)
    val factory = ViewModelFactory(db.dao)
    val viewModel: DetailViewModel = viewModel(factory = factory)

    val classOptions = viewModel.kategoriList

    var judul by rememberSaveable { mutableStateOf("") }
    var bahan by rememberSaveable { mutableStateOf("") }
    var step by rememberSaveable { mutableStateOf("") }
    var selectedKategori by rememberSaveable { mutableStateOf(classOptions[0]) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    var showDialog by remember { mutableStateOf(false)}

    fun onImageUriChange(uri: Uri?){
        imageUri = uri
    }

    LaunchedEffect(true){
        if (id == null) return@LaunchedEffect
        val data = viewModel.getResep(id) ?: return@LaunchedEffect
        judul = data.judul
        bahan = data.bahan
        step = data.step
        imageUri = if (data.imageUri != null) Uri.parse(data.imageUri) else null
    }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.kembali),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = {
                    if (id == null)
                        Text(text = stringResource(id = R.string.tambah_resep))
                    else
                        Text(text = stringResource(id = R.string.edit_resep))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {
                        if (judul == "" || bahan == "" || selectedKategori == "" || step == ""){
                            Toast.makeText(context, R.string.invalid, Toast.LENGTH_LONG).show()
                            return@IconButton
                        }
                        if(id == null){
                            viewModel.insert(judul, bahan, selectedKategori, step, imageUri)
                        }else{
                            viewModel.update(id, judul, bahan, selectedKategori, step, imageUri)
                        }
                        navController.popBackStack()}) {
                        Icon(imageVector = Icons.Outlined.Check,
                            contentDescription = stringResource(R.string.simpan),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (id != null){
                        DeleteAction { showDialog = true}
                        DisplayAlertDialog(
                            openDialog = showDialog,
                            onDismissRequest = { showDialog = false}) {
                            showDialog = false
                            viewModel.delete(id)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    ) { padding ->
        ResepRahasia(
            judul = judul,
            onJudulChange = {judul = it},
            bahan = bahan,
            onBahanChange = {bahan = it},
            step = step,
            onStepChange = {step = it},
            selectedKategori = selectedKategori,
            onSelectedKategoriChange = {selectedKategori = it},
            classOptions = classOptions,
            imageUri = imageUri,
            onImageUriChange = { onImageUriChange(it) },
            modifier = Modifier.padding(padding))
    }
}


@Composable
fun ResepRahasia(
    judul: String, onJudulChange: (String) -> Unit,
    bahan: String, onBahanChange: (String) -> Unit,
    step: String, onStepChange: (String) -> Unit,
    selectedKategori: String, onSelectedKategoriChange: (String) -> Unit,
    imageUri: Uri?, onImageUriChange:(Uri?) -> Unit,
    classOptions: List<String>,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            onImageUriChange(uri)
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Pick Image")
            }

            PickImageFromGallery(imageUri, onImageUriChange)
        }
        OutlinedTextField(
            value = judul,
            onValueChange = { onJudulChange(it) },
            label = { Text(text = stringResource(R.string.resep)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bahan,
            onValueChange = { onBahanChange(it) },
            label = { Text(text = stringResource(R.string.bahan)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            modifier = Modifier
                .fillMaxWidth()

        )
        OutlinedTextField(
            value = step,
            onValueChange = { onStepChange(it) },
            label = { Text(text = stringResource(R.string.step)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            modifier = Modifier
                .fillMaxWidth()

        )

        Column(
            modifier = Modifier
                .padding(top = 6.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .fillMaxWidth()
        ) {
            classOptions.forEach { classText ->
                ClassOption(
                    label = classText,
                    isSelected = selectedKategori == classText,
                    modifier = Modifier
                        .selectable(
                            selected = selectedKategori == classText,
                            onClick = { onSelectedKategoriChange(classText) },
                            role = Role.RadioButton
                        )
                        .fillMaxWidth()
                        .padding(16.dp)
                )

            }
        }
    }
}

@Composable
fun DeleteAction(delete: () -> Unit){
    var expanded by remember { mutableStateOf(false)}

    IconButton(onClick = { expanded = true }) {
        Icon(imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.lainnya),
            tint = MaterialTheme.colorScheme.primary
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.hapus))
                },
                onClick = {
                    expanded = false
                    delete()
                })
        }
    }

}

@Composable
fun ClassOption(label: String, isSelected: Boolean, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun PickImageFromGallery(
    imageUri: Uri?,
    onImageUriChange: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUri) {
        if (imageUri != null) {
            @Suppress("DEPRECATION")
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            bitmapState.value = bitmap
        } else {
            bitmapState.value = null
        }
    }

//    if (imageUri != null && bitmapState.value == null) {
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
//            @Suppress("DEPRECATION")
//            bitmapState.value = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
//        } else {
//            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
//            bitmapState.value = ImageDecoder.decodeBitmap(source)
//        }
//    }

    onImageUriChange(imageUri)

    bitmapState.value?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(400.dp)
                .padding(20.dp)
        )
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DetailScreenPreview() {
    Assessment2Theme {
        DetailScreen(rememberNavController())
    }
}