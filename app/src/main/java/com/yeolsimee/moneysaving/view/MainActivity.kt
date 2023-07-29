@file:OptIn(ExperimentalMaterial3Api::class)

package com.yeolsimee.moneysaving.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yeolsimee.moneysaving.BottomNavItem
import com.yeolsimee.moneysaving.R
import com.yeolsimee.moneysaving.domain.calendar.CalendarDay
import com.yeolsimee.moneysaving.domain.entity.category.CategoryWithRoutines
import com.yeolsimee.moneysaving.domain.entity.category.TextItem
import com.yeolsimee.moneysaving.ui.MyPageRouteCode
import com.yeolsimee.moneysaving.ui.PrText
import com.yeolsimee.moneysaving.ui.dialog.CategoryModifyDialog
import com.yeolsimee.moneysaving.ui.dialog.CategoryUpdateDialog
import com.yeolsimee.moneysaving.ui.dialog.TwoButtonOneTitleDialog
import com.yeolsimee.moneysaving.ui.snackbar.CustomSnackBarHost
import com.yeolsimee.moneysaving.ui.theme.Gray99
import com.yeolsimee.moneysaving.ui.theme.RoumoTheme
import com.yeolsimee.moneysaving.utils.DialogState
import com.yeolsimee.moneysaving.utils.executeForTimeMillis
import com.yeolsimee.moneysaving.utils.hasNotificationPermission
import com.yeolsimee.moneysaving.utils.notification.RoutineAlarmManager
import com.yeolsimee.moneysaving.view.category.CategoryViewModel
import com.yeolsimee.moneysaving.view.home.HomeScreen
import com.yeolsimee.moneysaving.view.home.RoutineCheckViewModel
import com.yeolsimee.moneysaving.view.home.RoutineDeleteViewModel
import com.yeolsimee.moneysaving.view.home.calendar.CalendarViewModel
import com.yeolsimee.moneysaving.view.home.calendar.FindAllMyRoutineViewModel
import com.yeolsimee.moneysaving.view.home.calendar.SelectedDateViewModel
import com.yeolsimee.moneysaving.view.login.LoginActivity
import com.yeolsimee.moneysaving.view.mypage.MyPageScreen
import com.yeolsimee.moneysaving.view.mypage.MyPageViewModel
import com.yeolsimee.moneysaving.view.recommend.RecommendScreen
import com.yeolsimee.moneysaving.view.routine.RoutineActivity
import com.yeolsimee.moneysaving.view.routine.RoutineModifyOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalLayoutApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var alarmState: State<Boolean>
    private lateinit var snackbarState: SnackbarHostState
    private lateinit var callback: OnBackPressedCallback
    private var pressedTime: Long = 0

    // Home
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val selectedDateViewModel: SelectedDateViewModel by viewModels()
    private val findAllMyRoutineViewModel: FindAllMyRoutineViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val navigator = Navigator()

    private val routineActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedDateViewModel.find(calendarViewModel.today)
            } else if (result.resultCode == MyPageRouteCode) {
                navigator.navigate(BottomNavItem.MyPage)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            snackbarState = remember { SnackbarHostState() }

            RoumoTheme(navigationBarColor = Color.Black) {
                MainScreenView(snackbarState)
            }

            val scope = rememberCoroutineScope()

            callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (pressedTime + 2000 > System.currentTimeMillis()) {
                        finishAffinity()
                    } else {
                        executeForTimeMillis(scope, 500) {
                            snackbarState.showSnackbar(
                                "한번 더 누르면 종료", duration = SnackbarDuration.Indefinite
                            )
                        }
                    }
                    pressedTime = System.currentTimeMillis()
                }
            }
            onBackPressedDispatcher.addCallback(this, callback)
        }
    }

    @Composable
    fun MainScreenView(snackbarState: SnackbarHostState) {
        val navController = rememberNavController()
        val floatingButtonVisible = remember { mutableStateOf(false) }
        val categoryModifyDialogState: MutableState<DialogState<CategoryWithRoutines>> =
            remember { mutableStateOf(DialogState(false, null)) }

        val routineCheckViewModel: RoutineCheckViewModel = hiltViewModel()
        val routineDeleteViewModel: RoutineDeleteViewModel = hiltViewModel()

        val today = calendarViewModel.today
        val dayList = calendarViewModel.dayList.collectAsState().value

        findAllMyRoutineViewModel.find(
            calendarViewModel.getFirstAndLastDate(dayList), today.month, dayList
        )
        selectedDateViewModel.find(today)

        val destination by navigator.destination.collectAsState()
        LaunchedEffect(destination) {
            if (navController.currentDestination?.route != destination.screenRoute) {
                navigateTo(navController, destination)
            }
        }
        val selectedDateState = remember { mutableStateOf(calendarViewModel.today) }
        Scaffold(
            content = {
                Box(
                    Modifier
                        .padding(it)
                        .background(Color.White)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Home.screenRoute
                    ) {
                        composable(BottomNavItem.Home.screenRoute) {
                            floatingButtonVisible.value = true

                            HomeScreen(
                                calendarViewModel = calendarViewModel,
                                selectedDateViewModel = selectedDateViewModel,
                                findAllMyRoutineViewModel = findAllMyRoutineViewModel,
                                routineCheckViewModel = routineCheckViewModel,
                                routineDeleteViewModel = routineDeleteViewModel,
                                floatingButtonVisible = floatingButtonVisible,
                                categoryModifyDialogState = categoryModifyDialogState,
                                selectedState = selectedDateState,
                                onItemClick = { routineId, categoryId ->
                                    val intent =
                                        Intent(this@MainActivity, RoutineActivity::class.java)
                                    intent.putExtra("routineId", routineId)
                                    intent.putExtra("routineType", RoutineModifyOption.Update)
                                    intent.putExtra("categoryId", categoryId)
                                    routineActivityLauncher.launch(intent)
                                },
                                onDelete = { routineId ->
                                    RoutineAlarmManager.delete(this@MainActivity, routineId)
                                }
                            )
                            CustomSnackBarHost(snackbarState)
                        }
                        composable(BottomNavItem.Recommend.screenRoute) {
                            floatingButtonVisible.value = false
                            RecommendScreen()
                        }
                        composable(BottomNavItem.MyPage.screenRoute) {
                            floatingButtonVisible.value = false

                            alarmState = myPageViewModel.alarmState.collectAsState()

                            val scope = rememberCoroutineScope()
                            MyPageScreen(alarmState = alarmState, onChangeAlarmState = {
                                if (hasNotificationPermission(requestPermissionLauncher)) {
                                    myPageViewModel.changeAlarmState()
                                    executeForTimeMillis(scope, 1000) {
                                        snackbarState.showSnackbar(
                                            message = if (alarmState.value) "알림이 해제되었어요!" else "알림이 설정되었어요!",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }, onLogout = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    myPageViewModel.logoutAndCancelAlarms(this@MainActivity) {
                                        val intent = Intent(
                                            this@MainActivity, LoginActivity::class.java
                                        )
                                        startActivity(intent)
                                        finishAffinity()
                                    }
                                }
                            }, onWithdraw = {
                                myPageViewModel.withdraw(this@MainActivity) {
                                    val intent =
                                        Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            }, openInternetBrowser = { url ->
                                startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                )
                            })

                            CustomSnackBarHost(snackbarState)
                        }
                    }
                }
            },
            floatingActionButton = {
                if (floatingButtonVisible.value) {
                    FloatingActionButton(
                        onClick = {
                            val intent = Intent(this@MainActivity, RoutineActivity::class.java)
                            intent.putExtra("routineType", RoutineModifyOption.Add)
                            routineActivityLauncher.launch(intent)
                        },
                        containerColor = Color.Black,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .padding(end = 12.dp, bottom = 24.dp)
                            .size(50.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.icon_plus),
                            contentDescription = "루틴 추가"
                        )
                    }
                }
            },
            bottomBar = {
                MainBottomNavigation(
                    navController,
                    categoryModifyDialogState,
                    selectedDateState
                )
            })
    }

    @Composable
    fun MainBottomNavigation(
        navController: NavHostController,
        categoryModifyDialogState: MutableState<DialogState<CategoryWithRoutines>>,
        selectedDateState: MutableState<CalendarDay>,
    ) {
        val items = listOf(
            BottomNavItem.Home, BottomNavItem.Recommend, BottomNavItem.MyPage
        )

        Column(modifier = Modifier) {
            val categoryUpdateDialogState: MutableState<Boolean> =
                remember { mutableStateOf(false) }
            val categoryDeleteDialogState: MutableState<Boolean> =
                remember { mutableStateOf(false) }

            val dialogState = categoryModifyDialogState.value
            if (dialogState.isShowing) {
                CategoryModifyDialog(
                    state = categoryModifyDialogState,
                    categoryUpdateDialogState,
                    categoryDeleteDialogState
                )
            }
            val categoryViewModel: CategoryViewModel = hiltViewModel()
            if (categoryUpdateDialogState.value) {
                val category = dialogState.data!!.getTextItem()
                categoryModifyDialogState.value = dialogState.copy(isShowing = false)
                CategoryUpdateDialog(
                    dialogState = categoryUpdateDialogState,
                    categoryName = remember { mutableStateOf(category.name) },
                    onConfirmClick = { categoryName ->
                        CoroutineScope(Dispatchers.Main).launch {
                            val result =
                                categoryViewModel.update(TextItem(category.id, categoryName))
                            if (result.isSuccess) {
                                selectedDateViewModel.find(selectedDateState.value)
                            }
                        }
                    }
                )
            }

            if (categoryDeleteDialogState.value) {
                val category = dialogState.data!!.getTextItem()
                categoryModifyDialogState.value = dialogState.copy(isShowing = false)
                TwoButtonOneTitleDialog(
                    text = "카테고리 삭제시 해당 루틴들도 함께 사라져요.\n" +
                            "해당 카테고리를 정말 삭제하시겠어요?",
                    dialogState = categoryDeleteDialogState,
                    onConfirmClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            categoryViewModel.delete(category).onSuccess {
                                selectedDateViewModel.find(selectedDateState.value)
                            }
                        }
                    },
                    onCancelClick = {}
                )
            }

            BottomNavigator(navController, items)
        }
    }

    @Composable
    private fun BottomNavigator(
        navController: NavHostController,
        items: List<BottomNavItem>,
    ) {
        NavigationBar(
            contentColor = Color.Black,
            containerColor = Color.Black,
            modifier = Modifier.height(66.dp),
            tonalElevation = 0.dp
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->

                val isSelected = currentRoute == item.screenRoute
                val resId = if (isSelected) item.pressedResId else item.normalResId
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.W400
                val labelColor = if (isSelected) Color.White else Gray99

                NavigationBarItem(
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                Modifier
                                    .height(3.dp)
                                    .width(50.dp)
                                    .clip(RoundedCornerShape(size = 2.5.dp))
                                    .background(color = if (isSelected) Color.White else Color.Black)
                            )
                            Spacer(Modifier.height(9.dp))
                            Icon(
                                painter = painterResource(id = resId),
                                contentDescription = item.title,
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                                    .padding(0.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            PrText(
                                item.title,
                                fontSize = 10.sp,
                                fontWeight = fontWeight,
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.1).sp,
                                color = labelColor,
                                softWrap = false,
                            )
                        }
                    },
                    selected = isSelected,
                    onClick = {
                        navigateTo(navController, item)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
    }

    private fun navigateTo(navController: NavHostController, item: BottomNavItem) {
        try {
            navController.navigate(item.screenRoute) {
                navController.graph.startDestinationRoute?.let {
                    popUpTo(it) { saveState = true }
                }
                launchSingleTop = true
                restoreState = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            myPageViewModel.changeAlarmState()
            executeForTimeMillis(CoroutineScope(Dispatchers.Main), 1000) {
                snackbarState.showSnackbar(
                    message = if (alarmState.value) "알림이 해제되었어요!" else "알림이 설정되었어요!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}
