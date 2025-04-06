package com.emendo.expensestracker.categories.subcategory

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
internal fun CreateSubcategoryRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateSubcategoryViewModel = hiltViewModel(),
) {
  //  ExpeScaffoldWithTopBar(
  //    title = stringResource(R.string.create_subcategory_title),
  //    onNavigationClick = navigator::navigateUp,
  //    actions = persistentListOf(
  //      MenuAction(
  //        icon = ExpeIcons.Check,
  //        onClick = onConfirmActionClick,
  //        contentDescription = stringResource(id = R.string.confirm),
  //      )
  //    )
  //  ) { paddingValues ->
  //    CreateSubcategoryScreen(
  //      modifier = modifier
  //        .fillMaxSize()
  //        .imePadding()
  //        .padding(paddingValues),
  //    )
  //  }
}

//@Composable
//private fun CreateSubcategoryScreen(
//  stateProvider: () -> CreateSubcategoryUiState,
//  modifier: Modifier = Modifier,
//) {
//  val focusRequester = remember { FocusRequester() }
//  val keyboardController = LocalSoftwareKeyboardController.current
//  LaunchedEffect(Unit) {
//    focusRequester.requestFocus()
//  }
//
//  Column(modifier) {
//    LazyColumn(contentPadding = PaddingValues(Dimens.margin_large_x)) {
//      uniqueItem(key = "topContent") {
//        ExpeTextFieldWithRoundedBackground(
//          placeholder = stringResource(id = R.string.title),
//          text = stateProvider().title.stringValue(),
//          onValueChange = onTitleChanged,
//          modifier = Modifier
//            .focusRequester(focusRequester)
//            .onFocusChanged {
//              if (it.isFocused) {
//                keyboardController?.show()
//              }
//            }
//        )
//        SelectRowWithIcon(
//          labelResId = R.string.icon,
//          imageVectorProvider = { stateProvider().icon.imageVector },
//          onClick = onIconSelectClick,
//        )
//        SelectRowWithColor(
//          labelResId = R.string.color,
//          colorProvider = { stateProvider().color },
//          onClick = onColorSelectClick,
//        )
//        Spacer(modifier = Modifier.height(Dimens.margin_small_x))
//      }
//    }
//    ExpeButton(
//      text = confirmButtonText,
//      onClick = onConfirmActionClick,
//      enabled = stateProvider().confirmButtonEnabled,
//    )
//    additionalBottomContent()
//  }
//}