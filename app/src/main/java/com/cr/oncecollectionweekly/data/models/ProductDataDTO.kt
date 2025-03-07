package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.ProductData
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDataDTO(
    @SerialName("id") @SerializedName("id") val id: String,
    @SerialName("sku") @SerializedName("sku") val sku: String,
    @SerialName("is_return") @SerializedName("is_return") val isReturn: Int,
    @SerialName("celebrity_id") @SerializedName("celebrity_id") val celebrityId: Int,
    @SerialName("name") @SerializedName("name") val name: String,
    @SerialName("attribute_set_id") @SerializedName("attribute_set_id") val attributeSetId: String,
    @SerialName("price") @SerializedName("price") val price: String,
    @SerialName("final_price") @SerializedName("final_price") val finalPrice: String,
    @SerialName("status") @SerializedName("status") val status: String,
    @SerialName("type") @SerializedName("type") val type: String,
    @SerialName("web_url") @SerializedName("web_url") val webUrl: String,
    @SerialName("brand_name") @SerializedName("brand_name") val brandName: String,
    @SerialName("brand") @SerializedName("brand") val brand: String,
    @SerialName("is_following_brand") @SerializedName("is_following_brand") val isFollowingBrand: Int,
    @SerialName("brand_banner_url") @SerializedName("brand_banner_url") val brandBannerUrl: String,
    @SerialName("is_salable") @SerializedName("is_salable") val isSalable: Boolean,
    @SerialName("is_new") @SerializedName("is_new") val isNew: Int,
    @SerialName("is_sale") @SerializedName("is_sale") val isSale: Int,
    @SerialName("is_trending") @SerializedName("is_trending") val isTrending: Int,
    @SerialName("is_best_seller") @SerializedName("is_best_seller") val isBestSeller: Int,
    @SerialName("image") @SerializedName("image") val image: String,
    @SerialName("created_at") @SerializedName("created_at") val createdAt: String,
    @SerialName("updated_at") @SerializedName("updated_at") val updatedAt: String,
    @SerialName("weight") @SerializedName("weight") val weight: String? = null,
    @SerialName("description") @SerializedName("description") val description: String,
    @SerialName("short_description") @SerializedName("short_description") val shortDescription: String? = null,
    @SerialName("how_to_use") @SerializedName("how_to_use") val howToUse: String? = null,
    @SerialName("manufacturer") @SerializedName("manufacturer") val manufacturer: String? = null,
    @SerialName("key_ingredients") @SerializedName("key_ingredients") val keyIngredients: String? = null,
    @SerialName("returns_and_exchanges") @SerializedName("returns_and_exchanges") val returnsAndExchanges: String? = null,
    @SerialName("shipping_and_delivery") @SerializedName("shipping_and_delivery") val shippingAndDelivery: String? = null,
    @SerialName("about_the_brand") @SerializedName("about_the_brand") val aboutTheBrand: String? = null,
    @SerialName("meta_title") @SerializedName("meta_title") val metaTitle: String,
    @SerialName("meta_keyword") @SerializedName("meta_keyword") val metaKeyword: String? = null,
    @SerialName("meta_description") @SerializedName("meta_description") val metaDescription: String,
    @SerialName("size_chart") @SerializedName("size_chart") val sizeChart: String? = null,
    @SerialName("wishlist_item_id") @SerializedName("wishlist_item_id") val wishlistItemId: Int,
    @SerialName("has_options") @SerializedName("has_options") val hasOptions: String,
    @SerialName("options") @SerializedName("options") val options: List<String> = emptyList(),
    @SerialName("bundle_options") @SerializedName("bundle_options") val bundleOptions: List<String> = emptyList(),
    @SerialName("configurable_option") @SerializedName("configurable_option") val configurableOption: List<ConfigurableOptionDTO> = emptyList(),
    @SerialName("remaining_qty") @SerializedName("remaining_qty") val remainingQty: Int,
    @SerialName("images") @SerializedName("images") val images: List<String> = emptyList(),
    @SerialName("upsell") @SerializedName("upsell") val upsell: List<String> = emptyList(),
    @SerialName("related") @SerializedName("related") val related: List<String> = emptyList(),
    @SerialName("review") @SerializedName("review") val review: ReviewDTO,
)

fun ProductDataDTO.toProductData() = ProductData(
    id = this.id,
    brandName =  this.brandName,
    sku = this.sku,
    name = this.name,
    price = this.price.toDouble(),
    image = this.image,
    description = this.description,
    images =  this.images,
    configurableOption =  this.configurableOption.map { it.toConfigurableOption() }
)