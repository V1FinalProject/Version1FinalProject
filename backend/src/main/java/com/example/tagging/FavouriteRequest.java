package com.example.tagging;

/** Body of PUT /api/nominations/{id}/favourite. */
public record FavouriteRequest(boolean favourite) {
}
