// The Wasm loader uses import.meta, which is a SyntaxError inside eval().
// The Kotlin dev config wraps every module in eval() via EvalSourceMapDevToolPlugin,
// so drop that plugin and use plain source maps instead.
// Dev only: production has no eval plugin and must not emit maps (webApp.js.map collides between targets).
if (config.mode === "development") {
    const webpack = require("webpack");
    config.plugins = config.plugins.filter(
        (plugin) => !(plugin instanceof webpack.EvalSourceMapDevToolPlugin)
    );
    config.devtool = "source-map";
}
