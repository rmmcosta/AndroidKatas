package com.littlelemon.myapplication

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.littlelemon.myapplication.ui.theme.MyApplicationTheme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Greeting("Android")
                        Spacer(modifier = Modifier.height(16.dp))//empty space
                        SimpleUI { showToast(it) }
                        Spacer(modifier = Modifier.height(16.dp))//empty space
                        MyUI()
                        Spacer(modifier = Modifier.height(32.dp))//empty space
                        DiscountSpinner(this@MainActivity)
                    }
                }
            }
        }
    }

    private fun showToast(msg: String = "Button Clicked") {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun DiscountSpinner(context: Context) {
    var discount by remember { mutableStateOf(0.0) }
    var spinResults by remember { mutableStateOf("") }
    Button(
        onClick = {
            discount = Christmas.spinDiscountWheel { msg ->
                spinResults += msg + "\n"
            }
        },
        modifier = Modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 36.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        Text(text = "Spin")
    }
    Button(onClick = {
        discount = 0.0
        spinResults = ""
        Christmas.reset()
    }) {
        Text(text = "Reset")
    }
    Text(text = "Discount: $discount")
    TextField(
        value = spinResults.ifBlank { "No spin results" },
        readOnly = true,
        onValueChange = { spinResults = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Composable
fun MyUI() {
    var submittedData by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column {
        // Other UI elements

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SimpleForm2 { name, birthDate ->
                submittedData = Pair(name, birthDate)
            }
        } else {
            SimpleForm { name, birthDate ->
                submittedData = Pair(name, birthDate)
            }
        }

        if (submittedData != null) {
            Text(text = "Submitted Data: ${submittedData?.first}, ${submittedData?.second}")
        }
    }
}

@Composable
fun SimpleForm(onSubmit: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    Column {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Birth Date") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = { onSubmit(name, birthDate) },
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp, minHeight = 36.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Text(text = "Submit")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SimpleForm2(onSubmit: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Birth Date") },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) showDialog.value = true
                },
            readOnly = true
        )

        if (showDialog.value) {
            DatePicker(onDateSelected = { date ->
                birthDate = date.toString()
                showDialog.value = false
            })
        }

        Button(
            onClick = { onSubmit(name, birthDate) },
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp, minHeight = 36.dp)
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Text(text = "Submit")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(onDateSelected: (LocalDate) -> Unit) {
    val selectedDates = remember { mutableStateOf<List<LocalDate>>(listOf()) }
    val disabledDates = listOf(
        LocalDate.now().minusDays(7),
        LocalDate.now().minusDays(12),
        LocalDate.now().plusDays(3),
    )
    CalendarDialog(
        state = rememberUseCaseState(visible = true,
            onCloseRequest = { onDateSelected(selectedDates.value.first()) }),
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH,
            disabledDates = disabledDates
        ),
        selection = CalendarSelection.Dates { newDates ->
            selectedDates.value = newDates
        },
    )
}

@Composable
fun SimpleUI(onClickExecute: (String) -> Unit) {
    var clicksCount = 0
    Button(
        onClick = {
            clicksCount++
            onClickExecute("Button clicked $clicksCount times")
        }, modifier = Modifier
            .defaultMinSize(
                minWidth = 64.dp, minHeight = 36.dp
            ) // set the minimum width and height
            .wrapContentWidth() // set the width to wrap the content
            .wrapContentHeight() // set the height to wrap the content
    ) {
        Text(text = "Button")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}