el = document.evaluate("//div[contains(@onmousedown,\"'BIRD_NAME'\")]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
if (el) {
  el.onmousedown(undefined);
}