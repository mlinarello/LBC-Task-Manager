const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require("copy-webpack-plugin");
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

module.exports = {
  optimization: {
    usedExports: true
  },
  entry: {
    homepage: path.resolve(__dirname, 'src', 'pages', 'homePage.js'),
    Registration: path.resolve(__dirname, 'src', 'pages', 'Registration.js'),
    login: path.resolve(__dirname, 'src', 'pages', 'LoginPage.js'),
    taskPage: path.resolve(__dirname, 'src', 'pages', 'TaskView.js')
  },
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].js',
  },
  devServer: {
    https: false,
    port: 8080,
    open: true,
    openPage: 'http://localhost:8080',
    // diableHostChecks, otherwise we get an error about headers and the page won't render
    disableHostCheck: true,
    contentBase: 'packaging_additional_published_artifacts',
    // overlay shows a full-screen overlay in the browser when there are compiler errors or warnings
    overlay: true
  },
  // proxy: [
  //     {
  //       context: [
  //         '/users/',
  //         '/comments',
  //         '/tasks'
  //       ],
  //       target: 'http://localhost:5001'
  //     }
  //   ],
  plugins: [
    new HtmlWebpackPlugin({
      template: './src/index.html',
      filename: 'index.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './src/Registration.html',
      filename: 'Registration.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './src/login.html',
      filename: 'login.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './src/homePage.html',
      filename: 'homePage.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './src/taskView.html',
      filename: 'taskView.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
      template: './src/tos.html',
      filename: 'tos.html',
      inject: false
    }),
    new CopyPlugin({
      patterns: [
        {
          from: path.resolve('src/css'),
          to: path.resolve("dist/css")
        }
      ]
    }),
    new CopyPlugin({
      patterns: [
        {
          from: path.resolve('src/resources/svg'),
          to: path.resolve("dist/svg")
        }
      ]
    }),
    new CopyPlugin({
      patterns: [
        {
          from: path.resolve('src/resources/images'),
          to: path.resolve("dist/images")
        }
      ]
    }),
    new CleanWebpackPlugin()
  ]
}
