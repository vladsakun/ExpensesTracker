package com.emendo.accounts.create

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class CreateAccountPreviewData : PreviewParameterProvider<CreateAccountScreenData> {
  val data = CreateAccountScreenData.getDefaultState()
  override val values: Sequence<CreateAccountScreenData> = sequenceOf(data)
}