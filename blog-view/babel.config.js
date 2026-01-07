module.exports = {
  presets: ["@vue/cli-plugin-babel/preset"],
  plugins: [
    ["@babel/plugin-transform-private-methods", { loose: true }],
    ["@babel/plugin-transform-class-properties", { loose: true }],
  ],
};
