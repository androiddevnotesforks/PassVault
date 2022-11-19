package com.mrntlu.PassVault.ui.widgets

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.amulyakhare.textdrawable.TextDrawable
import com.mrntlu.PassVault.R
import com.mrntlu.PassVault.models.PasswordItem
import com.mrntlu.PassVault.ui.theme.Red500
import com.mrntlu.PassVault.utils.getAsString

@Composable
fun OnlinePasswordListItem(
    index: Int,
    onEditClicked: (Int) -> Unit,
    onDeleteClicked: (Int) -> Unit,
    onItemClicked: (Int) -> Unit,
    password: PasswordItem
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }

    //TODO: More customization option
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .padding(horizontal = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        clipboardManager.setText(AnnotatedString(password.password))
                        Toast
                            .makeText(
                                context,
                                "${password.title} Coppied",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    },
                    onTap = {
                        onItemClicked(index)
                    }
                )
            },
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(start = 6.dp)
                .padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (password.imageUri != null) {
                var isImageLoading by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(password.imageColor.toULong())),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        model = ImageRequest.Builder(context)
                            .data(password.imageUri)
                            .listener(
                                onStart = {
                                    isImageLoading = true
                                },
                                onSuccess = { _, _ ->
                                    isImageLoading = false
                                },
                                onError = { _, _ ->
                                    isImageLoading = false
                                }
                            )
                            .build(),
                        contentDescription = stringResource(id = R.string.cd_image),
                    )

                    if (isImageLoading) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            } else {
                val drawable = remember { TextDrawable.builder().buildRound(
                    password.title.trim { it <= ' ' }.substring(0, 1),
                    Color(password.imageColor.toULong()).hashCode())
                }

                Image(
                    modifier = Modifier
                        .size(48.dp),
                    painter = rememberAsyncImagePainter(model = drawable),
                    contentDescription = stringResource(id = R.string.cd_image),
                )
            }

            Column(
                modifier = Modifier
                    .padding(
                        vertical = 8.dp,
                        horizontal = 12.dp
                    )
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = password.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                )

                Text(
                    text = password.username,
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 6.dp)
                    .padding(horizontal = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(end = 3.dp)
                        .size(22.dp),
                    onClick = {
                        clipboardManager.setText(AnnotatedString(password.getDecryptedPassword()))
                        Toast
                            .makeText(
                                context,
                                "${password.title} Coppied",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = stringResource(R.string.cd_copy),
                        tint = Color.Black,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .size(24.dp),
                    onClick = {
                        expanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(id = R.string.cd_item_menu),
                        tint = Color.Black,
                    )

                    OnlineItemDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        index = index,
                        onEditClicked = onEditClicked,
                        onDeleteClicked = onDeleteClicked,
                        onDetailsClicked = onItemClicked,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun OnlinePasswordListItemPreview() {
    OnlinePasswordListItem(0, {}, {}, {},PasswordItem("Test", "Test Title", null, "Test Password", Red500.getAsString(), false))
}

@Preview
@Composable
fun OnlinePasswordListItemLongPreview() {
    OnlinePasswordListItem(0, {}, {}, {},PasswordItem("Test User Name This is an Example", "Test Long Title This is an example of long text", "This is an example note", "Test Password", Red500.getAsString(),false))
}