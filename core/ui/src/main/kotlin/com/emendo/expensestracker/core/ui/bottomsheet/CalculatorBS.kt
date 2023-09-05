package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.CalculatorRow

@Composable
fun CalculatorBS(
  onChangeSignClick: () -> Unit,
  onClearClick: () -> Unit,
  onNumberClick: (number: Int) -> Unit,
  onConfirmClick: (result: Double) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(Dimens.margin_small_x),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
  ) {
    Row {
      Text(
        text = "1200,23 CZK",
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
      )
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = { },
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = Modifier
          .weight(2f)
          .fillMaxHeight(),
      ) {
        Text(text = stringResource(R.string.expense))
      }
      RoundedCornerSmallButton(
        onClick = { },
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = Modifier
          .weight(2f)
          .fillMaxHeight(),
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = "CZK\nCurrency",
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
          style = LocalTextStyle.current.copy(lineHeight = 15.sp),
        )
      }
      RoundedCornerSmallButton(
        onClick = { },
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = Modifier
          .weight(2f)
          .fillMaxHeight(),
      ) {
        Icon(imageVector = ExpIcons.Backspace, contentDescription = "clear")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
        colors = ButtonDefaults.filledTonalButtonColors(),
        contentPadding = PaddingValues(0.dp)
      ) {
        Icon(imageVector = ExpIcons.Add, contentDescription = stringResource(R.string.add))
      }
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "7")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "8")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "9")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier.size(Dimens.icon_button_size),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(),
      ) {
        Icon(imageVector = ExpIcons.Remove, contentDescription = stringResource(R.string.substract))
      }
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "4")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "5")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "6")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier.size(Dimens.icon_button_size),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(),
      ) {
        Icon(imageVector = ExpIcons.Close, contentDescription = "Close")
      }
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "1")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "2")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      ) {
        Text(text = "3")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier.size(Dimens.icon_button_size),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(),
      ) {
        Icon(
          painter = painterResource(id = R.drawable.division),
          contentDescription = "Close"
        )
      }
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = { },
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = Modifier
          .weight(2f)
          .fillMaxHeight(),
      ) {
        Text(text = ",")
      }
      RoundedCornerSmallButton(
        onClick = { },
        modifier = Modifier
          .weight(2f)
          .fillMaxHeight(),
      ) {
        Text(text = "0")
      }
      RoundedCornerSmallButton(
        onClick = { },
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = Modifier
          .weight(3f)
          .fillMaxHeight(),
      ) {
        Text(text = "Done")
      }
    }
  }
}