@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeolsimee.moneysaving.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yeolsimee.moneysaving.R
import com.yeolsimee.moneysaving.domain.entity.category.CategoryWithRoutines
import com.yeolsimee.moneysaving.ui.PrText
import com.yeolsimee.moneysaving.ui.theme.DeleteRed
import com.yeolsimee.moneysaving.ui.theme.Gray66
import com.yeolsimee.moneysaving.ui.theme.Gray99
import com.yeolsimee.moneysaving.ui.theme.pretendard
import com.yeolsimee.moneysaving.utils.DialogState
import com.yeolsimee.moneysaving.utils.onClick

@Composable
fun CategoryModifyDialog(
    state: MutableState<DialogState<CategoryWithRoutines>>,
    categoryUpdateDialogState: MutableState<Boolean>,
    categoryDeleteDialogState: MutableState<Boolean>,
) {
    if (state.value.isShowing) {
        ModalBottomSheet(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
            onDismissRequest = { state.value = state.value.copy(isShowing = false) },
            dragHandle = { },
            containerColor = Color.Transparent
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(vertical = 20.dp, horizontal = 28.dp)
                ) {
                    Box(
                        Modifier
                            .padding(vertical = 14.dp)
                            .fillMaxWidth()
                            .onClick {
                                categoryUpdateDialogState.value = true
                            }) {
                        PrText(
                            text = stringResource(R.string.update_category_name),
                            fontWeight = FontWeight.W600,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }
                    Box(
                        Modifier
                            .padding(vertical = 14.dp)
                            .fillMaxWidth()
                            .onClick {
                                categoryDeleteDialogState.value = true
                            }) {
                        PrText(
                            text = stringResource(R.string.delete_category),
                            fontWeight = FontWeight.W600,
                            fontSize = 15.sp,
                            color = DeleteRed
                        )
                    }
                }
                Spacer(
                    Modifier
                        .navigationBarsPadding()
                        .height(66.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryUpdateDialog(
    dialogState: MutableState<Boolean>,
    categoryName: MutableState<String>,
    onConfirmClick: (String) -> Unit,
) {
    if (dialogState.value) {
        Dialog(onDismissRequest = { dialogState.value = false }) {
            Box(
                modifier = Modifier
                    .width(263.dp)
                    .height(179.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(top = 28.dp, start = 28.dp)
            ) {
                Column {
                    PrText(text = "카테고리명 수정하기", fontWeight = FontWeight.W600, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    BasicTextField(
                        value = categoryName.value,
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontFamily = pretendard,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            letterSpacing = (-0.1).sp,
                        ), onValueChange = { t ->
                            if (t.length <= 14) {
                                categoryName.value = t
                            }
                        }, singleLine = true, decorationBox = { innerTextField ->
                            Box {
                                if (categoryName.value.isEmpty()) {
                                    PrText(
                                        text = "카테고리를 입력해주세요.",
                                        color = Gray99,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400
                                    )
                                }
                                innerTextField()
                            }
                        }, modifier = Modifier
                            .padding(top = 10.dp, start = 2.dp, bottom = 7.dp)
                            .fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .padding(end = 28.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.Black)
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, end = 8.dp, bottom = 16.dp)
                    ) {
                        TextButton(onClick = {
                            dialogState.value = false
                        }) {
                            PrText(
                                text = "취소",
                                color = Gray66,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.W400
                            )
                        }
                        TextButton(onClick = {
                            dialogState.value = false
                            onConfirmClick(categoryName.value)
                            categoryName.value = ""
                        }) {
                            PrText(
                                text = "확인",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CategoryUpdateDialogPreview() {
    CategoryUpdateDialog(
        dialogState = remember { mutableStateOf(true) },
        categoryName = remember { mutableStateOf("카테고리명") },
        onConfirmClick = {})
}

@Preview
@Composable
fun CategoryModifyDialogPreview() {
    CategoryModifyDialog(
        state = remember { mutableStateOf(DialogState(true, null)) },
        categoryUpdateDialogState = remember { mutableStateOf(false) },
        categoryDeleteDialogState = remember { mutableStateOf(false) }
    )
}