# Adding Images to Kolshi Shopping App

## Directory Structure for Images

Images should be placed in: `src/main/resources/images/`

Create subdirectories for each product category:
```
src/main/resources/images/
├── electronics/
├── clothing/
├── books/
└── home-garden/
```

## Product Images

For each product, add a PNG image with the product ID (in lowercase) as the filename.

**Naming Convention**: `{CATEGORY}/{PRODUCT_ID}.png`

Examples:
- `electronics/e001.png` - For Electronics product with ID "E001"
- `clothing/c001.png` - For Clothing product with ID "C001"
- `books/b001.png` - For Books product with ID "B001"
- `home-garden/h001.png` - For Home & Garden product with ID "H001"

## Image Specifications

- **Format**: PNG (recommended), JPG also supported
- **Size**: Any size (will be scaled to 180x120 pixels for product cards)
- **Quality**: 72-96 DPI recommended for web/screen display

## Fallback Behavior

If an image is not found for a product, the app will automatically display:
- A colored background based on the product category
- A category emoji (📱 for electronics, 👕 for clothing, etc.)

This ensures the app works perfectly even if some product images are missing!

## Example Product Images

To test the feature:
1. Create `src/main/resources/images/electronics/e001.png`
2. Add any 180x120 or larger PNG image
3. Rebuild: `mvn clean package`
4. Run the app and the image will display in the product card

The app will automatically scale images to fit the product card dimensions.
