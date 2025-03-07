package com.cr.oncecollectionweekly.presentation.features.productDetails

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.cr.oncecollectionweekly.R
import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.domain.models.ProductData
import com.cr.oncecollectionweekly.ui.components.NoInternetScreen
import com.cr.oncecollectionweekly.utils.ContentDisplayState
import com.cr.oncecollectionweekly.utils.DataError
import com.cr.oncecollectionweekly.utils.DataResultState
import com.cr.oncecollectionweekly.utils.toString
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val productData by viewModel.productData.collectAsStateWithLifecycle()
    val isInternetConnected by viewModel.isInternetConnected.collectAsStateWithLifecycle()

    if (isInternetConnected) {
        ProductDetailScreenContent(
            productData = productData,
            retry = viewModel::getData,
            refreshData = viewModel::refreshData
        )
    } else {
        NoInternetScreen()
    }
}

@Composable
private fun ProductDetailScreenContent(
    productData: DataResultState<FetchProductDetailsResponse>,
    retry: () -> Unit = {},
    refreshData: () -> Unit = {},
) {

    when(productData) {
        DataResultState.Idle, DataResultState.Loading -> {
            ProductDetailScreenLoadState()
        }
        is DataResultState.Error -> {
            ProductDetailErrorScreen(
                error = productData.error,
                retry = retry
            )
        }
        is DataResultState.Success -> {
            ProductDetailScreenContent(
                productData = productData.data.data!!,
                contentDisplayState = productData.contentDisplayState,
                refreshData = refreshData
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreenContent(
    productData: ProductData,
    contentDisplayState: ContentDisplayState? = null,
    refreshData: () -> Unit = {},
) {

    var quantity by rememberSaveable { mutableIntStateOf(1) }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val spannedDescription = remember(productData.description) {
        HtmlCompat.fromHtml(productData.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = contentDisplayState == ContentDisplayState.Refreshing,
        onRefresh = refreshData
    )

    val carouselState = rememberCarouselState { productData.images.count() }
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = productData.name,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector =  if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.IosShare, contentDescription = "Share Product")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.ShoppingBag, contentDescription = "Add to Bag")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Add to bag" ,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        border = BorderStroke( width = 2.dp, color = MaterialTheme.colorScheme.onBackground ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Share",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }


                }
            }


        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {

                    HorizontalUncontainedCarousel(
                        state = carouselState,
                        modifier = Modifier.fillMaxSize(),
                        itemWidth = LocalDensity.current.run { (LocalConfiguration.current.screenWidthDp.dp).toPx() }.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(carouselState)
                    ) { i ->

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(productData.images[i])
                                .build(),
                            contentDescription = productData.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                // Detail and Pricing Section ------------------------------------------------------

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    Column (
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {

                        Text(
                            text = productData.brandName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = productData.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "SKU: ${productData.sku}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }

                    Text(
                        text = String.format("%.2f KWD", productData.price),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Color:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(
                        items = productData.configurableOption.flatMap { it.attributes },
                        key = {
                            it.optionId
                        }
                    ) { attribute ->

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                                .padding(4.dp),
                        ) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(attribute.images.first())
                                    .build(),
                                contentDescription = productData.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tabby Card ----------------------------------------------------------------------

                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {

                    Row (
                        modifier = Modifier.fillMaxWidth()
                            .padding( horizontal =  4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column (
                            modifier = Modifier.weight(1f)
                                .padding(start = 10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "or 4 interest-free payments",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = String.format("%.2f KWD", productData.price.div(4)),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )

                        }

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.tabby_logo)
                                .build(),
                            contentDescription = productData.name,
                            modifier = Modifier
                                .width(80.dp),
                            contentScale = ContentScale.Fit
                        )

                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Section ----------------------------------------------------------------

                Text(
                    text = "Quantity:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    FilledIconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease quantity",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box (
                        modifier = Modifier
                            .defaultMinSize(minWidth = 24.dp, minHeight = 48.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RectangleShape
                            )
                            .padding(horizontal = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledIconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase quantity",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description Section -------------------------------------------------------------

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PRODUCT INFORMATION",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }

                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = spannedDescription.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                )

                Spacer(modifier = Modifier.height(4.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    thickness = 2.dp
                )

                Spacer(modifier = Modifier.height(4.dp))

            }

            PullRefreshIndicator(
                refreshing = contentDisplayState == ContentDisplayState.Refreshing,
                state = pullRefreshState,
                colors = PullRefreshIndicatorDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreenLoadState() {

    val shimmerInstance = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .shimmer(shimmerInstance)
                            .background(
                                MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(16.dp)
                            ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector =  Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.IosShare, contentDescription = "Share Product")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.ShoppingBag, contentDescription = "Add to Bag")
                    }
                },
            )
        },
        bottomBar = {
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Add to bag" ,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        border = BorderStroke( width = 2.dp, color = MaterialTheme.colorScheme.onBackground ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Share",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }


                }
            }


        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(horizontal = 16.dp)
                    .shimmer(shimmerInstance)
                    .background(
                        MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(16.dp)
                    ),
                )

            Spacer(modifier = Modifier.height(8.dp))

            // Detail and Pricing Section ------------------------------------------------------

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column (
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {

                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(20.dp)
                            .shimmer(shimmerInstance)
                            .background(
                                MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(16.dp)
                            ),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(250.dp)
                            .height(20.dp)
                            .shimmer(shimmerInstance)
                            .background(
                                MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(16.dp)
                            ),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(20.dp)
                            .shimmer(shimmerInstance)
                            .background(
                                MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(16.dp)
                            ),
                    )

                }

                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .shimmer(shimmerInstance)
                        .background(
                            MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(16.dp)
                        ),
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Color:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {

                items(count = 10) {

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .shimmer(shimmerInstance),
                    )

                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity Section ----------------------------------------------------------------

            Text(
                text = "Quantity:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                FilledIconButton(
                    onClick = {  },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RectangleShape
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box (
                    modifier = Modifier
                        .defaultMinSize(minWidth = 24.dp, minHeight = 48.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RectangleShape
                        )
                        .shimmer(shimmerInstance)
                        .padding(horizontal = 36.dp),
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilledIconButton(
                    onClick = {  },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RectangleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description Section -------------------------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRODUCT INFORMATION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand"
                    )
                }

            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 16.dp)
                    .shimmer(shimmerInstance)
                    .background(
                        MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(16.dp)
                    ),
            )

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                thickness = 2.dp
            )

            Spacer(modifier = Modifier.height(4.dp))

        }

    }

}

@Composable
fun ProductDetailErrorScreen(
    error: DataError,
    retry: () -> Unit
) {

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
    ) { paddingValues ->

        Column (
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = error.toString(LocalContext.current),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = retry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RectangleShape
            ) {
                Text(
                    text = "Retry" ,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

        }

    }

}

