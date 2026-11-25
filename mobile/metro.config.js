// Learn more: https://docs.expo.dev/guides/customizing-metro
const { getDefaultConfig } = require('expo/metro-config')

const config = getDefaultConfig(__dirname)
// the offline note viewer (assets/renderer/note-viewer.html) ships as an asset
config.resolver.assetExts.push('html')

module.exports = config
