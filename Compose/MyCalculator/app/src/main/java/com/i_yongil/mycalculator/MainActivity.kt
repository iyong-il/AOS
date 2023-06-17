@file:OptIn(ExperimentalMaterial3Api::class)

package com.i_yongil.mycalculator

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.i_yongil.mycalculator.ui.theme.ActionButtonBgColor
import com.i_yongil.mycalculator.ui.theme.MyCalculatorTheme
import com.i_yongil.mycalculator.ui.theme.Purple40
import com.i_yongil.mycalculator.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch
import androidx.compose.material3.Card as Card

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "메인"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Calculator()
                }
            }
        }
    }
}

@Composable
fun Calculator() {

    val numbers = listOf<Int>(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    val actions: Array<CalculateAction> = CalculateAction.values()

    val buttons = listOf(
        CalculateAction.Divide,
        7, 8, 9, CalculateAction.Multiply,
        4, 5, 6, CalculateAction.Minus,
        1, 2, 3, CalculateAction.Plus,
        0
    )

    // 첫번째 입력
    var firstInput by remember { mutableStateOf("0") }
    // 두번째 입력
    var secondInput by remember { mutableStateOf("") }

    // 선택된 연산자
    val selectedAction: MutableState<CalculateAction?> = remember {
        mutableStateOf(null)
    }

    // 현재 선택된 액션 심볼
    val selectedSymbol: String = selectedAction.value?.symbol ?: ""

    // 계산기록
    val calculateHistories: MutableState<List<String>> = remember { mutableStateOf(emptyList()) }

    val coroutinenScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    var isCalculateHistoryVisible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            onClick = {
                isCalculateHistoryVisible = !isCalculateHistoryVisible
            }
        ) {
            Text(
                text = if (isCalculateHistoryVisible) "계산 기록 안보기" else "계산기록 보기",
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .padding(3.dp)
            )
        }

        AnimatedVisibility(visible = isCalculateHistoryVisible, modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
                reverseLayout = true,
                content = {
                    items(calculateHistories.value) { aHistory ->
                        Text(text = aHistory, modifier = Modifier.background(PurpleGrey80))
                    }
                })
        }

        Spacer(modifier = Modifier.weight(if(isCalculateHistoryVisible) 0.1f else 1f))

        // 뷰를 그리는 부분.
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    NumberText(
                        firstInput = firstInput,
                        secondInput = secondInput,
                        symbol = selectedSymbol,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                item(span = { GridItemSpan(2) }) {
                    ActionButton(action = CalculateAction.AllClear,
                        onClicked = {
                            firstInput = "0"
                            secondInput = ""
                            selectedAction.value = null
                        })
                }

                item(span = { GridItemSpan(1) }) {
                    ActionButton(action = CalculateAction.Delete,
                        onClicked = {

                            if (secondInput.length > 0) {
                                secondInput = secondInput.dropLast(1)
                                return@ActionButton
                            }

                            if (selectedAction.value != null) {
                                selectedAction.value = null
                                return@ActionButton
                            }

                            firstInput = if (firstInput.length == 1) "0" else firstInput.dropLast(1)
                        })
                }

                // 숫자, 액션
                items(buttons) { aButton ->

                    when (aButton) {
                        is CalculateAction -> ActionButton(aButton,
                            selectedAction.value,
                            onClicked = {
                                selectedAction.value = aButton
                            }) // 액션버튼

                        is Int -> NumberButton(aButton, onClicked = {
                            if (selectedAction.value == null) {
                                if (firstInput == "0") firstInput =
                                    aButton.toString() else firstInput += aButton
                            } else {
                                secondInput += aButton
                            }
                        }) // 숫자버튼
                    }

                }

                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    ActionButton(action = CalculateAction.Calculate,
                        onClicked = {

                            if(secondInput.isEmpty()) {
                                return@ActionButton
                            }

                            selectedAction.value?.let {
                                val result = doCalculate(
                                    firstNumber = firstInput.toFloat(),
                                    secondNumber = secondInput.toFloat(),
                                    action = it
                                )

                                // 계산기록 업데이트
                                val calculateHistory =
                                    "$firstInput $selectedSymbol $secondInput = $result"
                                calculateHistories.value += calculateHistory

                                // 맨 위로 스크롤 시키기
                                coroutinenScope.launch {
                                    scrollState.animateScrollToItem(
                                        calculateHistories.value.size
                                    )
                                }

                                firstInput = result.toString()
                                secondInput = ""
                                selectedAction.value = null
                                Log.d(TAG, "계산결과 ${result}")
                            } ?: Log.d(TAG, "선택된 연산이 없습니다.")

                        })
                }


            })
    }

}

@Composable
fun NumberText(
    firstInput: String,
    secondInput: String,
    symbol: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {

        Text(
            text = firstInput,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            lineHeight = 50.sp,
            maxLines = 1,
            color = Color.Black
        )

        Text(
            text = symbol,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            lineHeight = 50.sp,
            maxLines = 1,
            color = Purple40
        )

        Text(
            text = secondInput,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            lineHeight = 50.sp,
            maxLines = 1,
            color = Color.Black
        )

    }
}

@Composable
fun ActionButton(
    action: CalculateAction,
    selectedAction: CalculateAction? = null,
    onClicked: (() -> Unit)? = null
) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedAction == action) Purple40 else ActionButtonBgColor,
            contentColor = if (selectedAction == action) Color.White else Color.Black
        ),
        onClick = {
            onClicked?.invoke()
        }
    ) {
        Text(
            text = action.symbol,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NumberButton(number: Int, onClicked: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = onClicked
    ) {
        Text(
            text = number.toString(),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// 계산처리
fun doCalculate(
    firstNumber: Float,
    secondNumber: Float,
    action: CalculateAction
): Float? {
    return when (action) {
        CalculateAction.Plus -> firstNumber + secondNumber
        CalculateAction.Minus -> firstNumber - secondNumber
        CalculateAction.Multiply -> firstNumber * secondNumber
        CalculateAction.Divide -> firstNumber / secondNumber
        else -> null
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyCalculatorTheme {}
}