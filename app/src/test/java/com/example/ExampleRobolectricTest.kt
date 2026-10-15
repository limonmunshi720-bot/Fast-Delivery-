package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MarketplaceRepository
import com.example.model.CartItem
import com.example.model.CustomerAddress
import com.example.model.Language
import com.example.model.LanguageHelper
import com.example.model.OrderStatus
import com.example.model.ProductCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Fast Delivery", appName)
    }

    @Test
    fun `test bengali numeral and BDT currency formatting`() {
        val bnFormatted = LanguageHelper.formatBdt(1250.0, Language.BN)
        assertEquals("৳ ১,২৫০", bnFormatted)

        val enFormatted = LanguageHelper.formatBdt(1250.0, Language.EN)
        assertEquals("৳ 1,250", enFormatted)
    }

    @Test
    fun `test server-side order calculation and cash on delivery`() {
        val repo = MarketplaceRepository()
        val testProduct = repo.products.value.first() // Miniket rice ৳370

        val address = CustomerAddress(
            recipientName = "Fahim Shahriar",
            phone = "01711-223344",
            division = "Dhaka",
            district = "Dhaka City",
            thana = "Dhanmondi",
            streetAddress = "Road 5",
            deliveryNotes = "Call at door"
        )

        // Inside Dhaka delivery fee should be 60.0
        assertTrue(address.isInsideDhaka)

        val order = repo.placeOrder(
            items = listOf(CartItem(product = testProduct, quantity = 2)),
            address = address,
            couponCode = "FAST50"
        )

        // Subtotal = 370 * 2 = 740.0
        // Delivery = 60.0
        // Discount (FAST50) = 50.0
        // Total = 740 + 60 - 50 = 750.0
        assertEquals(740.0, order.subtotal, 0.01)
        assertEquals(60.0, order.deliveryFee, 0.01)
        assertEquals(50.0, order.discount, 0.01)
        assertEquals(750.0, order.totalAmount, 0.01)
        assertEquals(OrderStatus.PENDING, order.status)
        assertEquals("Cash on Delivery (ক্যাশ অন ডেলিভারি)", order.paymentMethod)
    }

    @Test
    fun `test vendor product approval workflow`() {
        val repo = MarketplaceRepository()
        val initialLiveCount = repo.products.value.size
        val initialPendingCount = repo.pendingProducts.value.size

        // Admin approves a pending product
        val pendingId = repo.pendingProducts.value.first().id
        repo.approveProduct(pendingId)

        assertEquals(initialPendingCount - 1, repo.pendingProducts.value.size)
        assertEquals(initialLiveCount + 1, repo.products.value.size)
    }
}
