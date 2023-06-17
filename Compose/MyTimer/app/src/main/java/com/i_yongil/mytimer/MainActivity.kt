package com.i_yongil.mytimer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.i_yongil.mytimer.ui.theme.MyTimerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimerScreen()
                }
            }
        }
    }
}

@Composable
fun TimerScreen() {

    // 데이터
    var timerCount by remember { mutableStateOf(0) }
    var isActive by remember { mutableStateOf(false) }

    val snackBarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        while(true) {
            delay(1000L) // 1초
            if(isActive && timerCount > 0) timerCount--
            if(isActive && timerCount == 0) isActive = false
        }
    }

    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    Text(timerCount.toString(),
        fontSize = 120.sp,
        modifier = Modifier.padding(vertical = 100.dp)
    )

        // 타이머 토글 버튼
        Button(onClick = {
            if(timerCount == 0) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        "⏰ 시간을 먼저 설정해주세요!",
                        actionLabel = "닫기",
                        SnackbarDuration.Short
                    ).let {
//                        when(it) {
//                            SnackbarResult.Dismissed -> Log.d("TAG", "스낵바 닫힘")
//                            SnackbarResult.ActionPerformed -> Log.d("TAG", "스냑바 닫힘버튼 클릭")
//                            else -> Log.d("TAG", "종료")
//                        }
                    }
                    return@launch
                }
                return@Button
            }
            // 타이머 토글
            isActive = !isActive
        }) {
            Text(
                if(isActive) "종료" else "시작",
            fontSize = 30.sp,
            modifier = Modifier.padding(10.dp)
            )
        }

        AnimatedVisibility(visible = !isActive) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                //타이머 설정
                Text(text = "타이머 설정",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(10.dp))

                Row() {
                    Button(
                        modifier = Modifier.size(100.dp),
                        onClick = {
                            //TODO:: 시간 증가
                            timerCount += 1
                        }) {
                        Text(text = "+",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(10.dp))
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    Button(
                        modifier = Modifier.size(100.dp),
                        onClick = {
                            //TODO:: 시간 감소
                            if(timerCount > 0) timerCount -= 1
                        }) {
                        Text(text = "-",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }


        // 스낵바가 보여지는 부분
        SnackbarHost(hostState = snackBarHostState)




    }
}

private fun SnackbarHostState.showSnackbar(s: String, actionLabel: String, short: SnackbarDuration) {

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTimerTheme {
//        Greeting("Android")
    }
}