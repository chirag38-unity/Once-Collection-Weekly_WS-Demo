package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.ProductData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDataDTO(
    @SerialName("id") val id: String,
    @SerialName("sku") val sku: String,
    @SerialName("is_return") val isReturn: Int,
    @SerialName("celebrity_id") val celebrityId: Int,
    @SerialName("name") val name: String,
    @SerialName("attribute_set_id") val attributeSetId: String,
    @SerialName("price") val price: String,
    @SerialName("final_price") val finalPrice: String,
    @SerialName("status") val status: String,
    @SerialName("type") val type: String,
    @SerialName("web_url") val webUrl: String,
    @SerialName("brand_name") val brandName: String,
    @SerialName("brand") val brand: String,
    @SerialName("is_following_brand") val isFollowingBrand: Int,
    @SerialName("brand_banner_url") val brandBannerUrl: String,
    @SerialName("is_salable") val isSalable: Boolean,
    @SerialName("is_new") val isNew: Int,
    @SerialName("is_sale") val isSale: Int,
    @SerialName("is_trending") val isTrending: Int,
    @SerialName("is_best_seller") val isBestSeller: Int,
    @SerialName("image") val image: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("weight") val weight: String? = null,
    @SerialName("description") val description: String,
    @SerialName("short_description") val shortDescription: String? = null,
    @SerialName("how_to_use") val howToUse: String? = null,
    @SerialName("manufacturer") val manufacturer: String? = null,
    @SerialName("key_ingredients") val keyIngredients: String? = null,
    @SerialName("returns_and_exchanges") val returnsAndExchanges: String? = null,
    @SerialName("shipping_and_delivery") val shippingAndDelivery: String? = null,
    @SerialName("about_the_brand") val aboutTheBrand: String? = null,
    @SerialName("meta_title") val metaTitle: String,
    @SerialName("meta_keyword") val metaKeyword: String? = null,
    @SerialName("meta_description") val metaDescription: String,
    @SerialName("size_chart") val sizeChart: String? = null,
    @SerialName("wishlist_item_id") val wishlistItemId: Int,
    @SerialName("has_options") val hasOptions: String,
    @SerialName("options") val options: List<String> = emptyList(),
    @SerialName("bundle_options") val bundleOptions: List<String> = emptyList(),
    @SerialName("configurable_option") val configurableOption: List<ConfigurableOptionDTO> = emptyList(),
    @SerialName("remaining_qty") val remainingQty: Int,
    @SerialName("images") val images: List<String> = emptyList(),
    @SerialName("upsell") val upsell: List<String> = emptyList(),
    @SerialName("related") val related: List<String> = emptyList(),
    @SerialName("review") val review: ReviewDTO,
)

fun ProductDataDTO.toProductData() = ProductData(
    id = this.id,
    brandName =  this.brandName,
    sku = this.sku,
    name = this.name,
    price = this.price,
    image = this.image,
    description = this.description,
    images =  this.images,
    configurableOption =  this.configurableOption.map { it.toConfigurableOption() }
)