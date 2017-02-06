CREATE TABLE IF NOT EXISTS image (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    color_id INTEGER REFERENCES color_lookup(_id),
    imageurl_id INTEGER REFERENCES imageurl_lookup(_id)
);

CREATE TABLE IF NOT EXISTS imageurl_lookup (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    imageurl TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS color_lookup (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    color TEXT NOT NULL
);

-- INDEX

CREATE INDEX idx_imageurl_lookup_imageurl ON imageurl_lookup(imageurl);
CREATE INDEX idx_color_lookup_color ON color_lookup(color);


