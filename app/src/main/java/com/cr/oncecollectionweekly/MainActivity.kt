package com.cr.oncecollectionweekly

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cr.oncecollectionweekly.presentation.navigation.Routes
import com.cr.oncecollectionweekly.ui.theme.OnceCollectionWeeklyTheme
import androidx.navigation.compose.navigation
import com.cr.oncecollectionweekly.presentation.features.productDetails.ProductDetailScreen
import com.cr.oncecollectionweekly.presentation.features.productDetails.ProductDetailViewModel
import com.cr.oncecollectionweekly.utils.AndroidConnectivityObserver
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            OnceCollectionWeeklyTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.StoreGraph
                ) {

                    navigation<Routes.StoreGraph>(
                        startDestination = Routes.ProductDetailsPage
                    ) {

                        composable<Routes.ProductDetailsPage>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {

                            val viewModel = koinViewModel<ProductDetailViewModel>()

                            ProductDetailScreen(
                                viewModel = viewModel,
                            )

                        }

                    }

                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OnceCollectionWeeklyTheme {
        Greeting("Android")
    }
}