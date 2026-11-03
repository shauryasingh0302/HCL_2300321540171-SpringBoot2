package com.hcltech.onlineshopping.controller;

import com.hcltech.onlineshopping.entity.Cart;
import com.hcltech.onlineshopping.entity.CartItem;
import com.hcltech.onlineshopping.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add/{customerId}/{productId}")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @RequestParam(required = false) Integer quantity,
            @RequestBody(required = false) Map<String, Object> body) {
        int qty = 1;
        if (quantity != null) {
            qty = quantity;
        } else if (body != null && body.containsKey("quantity")) {
            qty = Integer.parseInt(body.get("quantity").toString());
        }
        Cart cart = cartService.addProductToCart(customerId, productId, qty);
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long customerId) {
        Cart cart = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam(required = false) Integer quantity,
            @RequestBody(required = false) Map<String, Object> body) {
        int qty = 1;
        if (quantity != null) {
            qty = quantity;
        } else if (body != null && body.containsKey("quantity")) {
            qty = Integer.parseInt(body.get("quantity").toString());
        }
        CartItem cartItem = cartService.updateCartItemQuantity(cartItemId, qty);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.ok("Cart item removed successfully");
    }

    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<String> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
