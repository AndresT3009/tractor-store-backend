package com.tractorstore.catalog.domain;

/**
 * Distancia euclidiana entre dos colores hexadecimales en el espacio RGB.
 *
 * <p>Usada por {@link ProductCatalog#recommend} para encontrar variantes de color similar. No es un
 * modelo perceptual de color (como CIE76/CIEDE2000); para este catálogo, la distancia RGB simple es
 * suficiente y evita traer una dependencia externa.
 */
final class ColorDistance {

  private ColorDistance() {}

  static double between(String hexA, String hexB) {
    int[] rgbA = toRgb(hexA);
    int[] rgbB = toRgb(hexB);
    double dr = rgbA[0] - rgbB[0];
    double dg = rgbA[1] - rgbB[1];
    double db = rgbA[2] - rgbB[2];
    return Math.sqrt(dr * dr + dg * dg + db * db);
  }

  private static int[] toRgb(String hex) {
    String value = hex.startsWith("#") ? hex.substring(1) : hex;
    return new int[] {
      Integer.parseInt(value.substring(0, 2), 16),
      Integer.parseInt(value.substring(2, 4), 16),
      Integer.parseInt(value.substring(4, 6), 16)
    };
  }
}
