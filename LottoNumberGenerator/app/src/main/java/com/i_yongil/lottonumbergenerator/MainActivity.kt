package com.i_yongil.lottonumbergenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.i_yongil.lottonumbergenerator.ui.theme.LottoNumberGeneratorTheme
import kotlin.random.Random

fun Color.Companion.random(): Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LottoNumberGeneratorTheme {

                // 데이터
                val buttonTitle = "로또번호 생성"

                // 번호생성 횟수
                var generatedCount by remember { mutableStateOf(0) }

                // 로또번호
                var num01 by remember { mutableStateOf(0) }
                var num02 by remember { mutableStateOf(0) }
                var num03 by remember { mutableStateOf(0) }
                var num04 by remember { mutableStateOf(0) }
                var num05 by remember { mutableStateOf(0) }
                var num06 by remember { mutableStateOf(0) }
                var num07 by remember { mutableStateOf(0) }

                // 1 ~ 45 까지의 번호 범위
                val lottoNumberRange = (1..45)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // 세로로 만들 경우 == VStack
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 가로로 만들 경우 == HStack
                        // 로또 번호
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 10.dp)
                                .padding(vertical = 40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            lottoBall(number = num01)
                            lottoBall(number = num02)
                            lottoBall(number = num03)
                            lottoBall(number = num04)
                            lottoBall(number = num05)
                            lottoBall(number = num06)
                            lottoBall(number = num07)
                        }

                        Spacer(modifier = Modifier.height(150.dp))

                        Text(text = "생성된 횟수 : $generatedCount", fontSize = 30.sp)

                        Spacer(modifier = Modifier.height(30.dp))

                        // 로또 번호 생성 버튼
                       Button(onClick = {
                            num01 = lottoNumberRange.random()
                            num02 = lottoNumberRange.random()
                            num03 = lottoNumberRange.random()
                            num04 = lottoNumberRange.random()
                            num05 = lottoNumberRange.random()
                            num06 = lottoNumberRange.random()
                            num07 = lottoNumberRange.random()
//                           generatedCount += 1
                           generatedCount ++
                        }) {
                            Text(text = buttonTitle, fontSize = 30.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun lottoBall(number: Int) {

    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.random()),
        contentAlignment = Alignment.Center
    ) {
        Text(number.toString(),
            fontSize = 20.sp,
            color = Color.White
        )
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LottoNumberGeneratorTheme {
        Greeting("Android")
    }
}