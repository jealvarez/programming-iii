module.exports = function(grunt) {

  // Include dependencies
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-clean');

  // Configuration of each dependency
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    // CSS Minify
    cssmin: {
      combine: {
        files: {
          'src/main/webapp/resources/css/styles.min.css': ['src/assets/css/*.css']
        }        
      }
    },
    // JS Minify
    uglify: {
      options: {
        sourceMap: true
      },
      build: {
        files: {
          'src/main/webapp/resources/js/main.min.js': [
          'src/assets/js/jquery.js',
          'src/assets/js/*.js'
          ]
        }
      }
    },
    // Copy Files
    copy: {
      js:
      {
        expand : true,
        cwd    : 'bower_components/',
        src    : [
        'bootstrap/dist/js/bootstrap.js',
        'jquery/dist/jquery.js'
        ],
        dest   : 'src/assets/js/',
        flatten: true,
        filter : 'isFile'
      },
      css: {
        expand : true,
        cwd    : 'bower_components/',
        src    : [
        'bootstrap/dist/css/bootstrap-theme.css',
        'bootstrap/dist/css/bootstrap.css',
        'normalize.css/normalize.css'
        ],
        dest   : 'src/assets/css/',
        flatten: true,
        filter : 'isFile'
      },
      fonts: {
        expand : true,
        cwd    : 'bower_components/',
        src    : [
        'bootstrap/fonts/*'
        ],
        dest   : 'src/main/webapp/resources/fonts/',
        flatten: true,
        filter : 'isFile'
      },
      copy_min: {
        files: [
        {
          expand : true,
          cwd    : 'src/',
          src    : [
          'css/*.min.css'
          ],
          dest   : 'src/main/webapp/resources/css/',
          flatten: true,
          filter : 'isFile'
        },
        {
          expand : true,
          cwd    : 'src/',
          src    : [
          'js/*.min.js'
          ],
          dest   : 'src/main/webapp/resources/js/',
          flatten: true,
          filter : 'isFile'          
        }
        ]
      }
    },
    clean: {
      js : ['src/assets/js/*', '!src/assets/js/main.min.js'],
      css: ['src/assets/css/*', '!src/assets/css/styles.min.css']
    }
  });

  // Register tasks
  grunt.registerTask('copy-assets', 'Copy the assets from bower_components to css, js and fonts folders.', ['copy:css', 'copy:js', 'copy:fonts']);

  grunt.registerTask('delete-assets', 'Delete assets folder with all content inside.', function() {
    grunt.file.delete('src/assets', {force:true});
  });

  grunt.registerTask('compress-assets', 'Compress all JavaScript files in just one.', function() {
    grunt.task.run('cssmin', 'uglify', 'delete-assets');
  });

  grunt.registerTask('create-assets', 'Minify and copy all assets into webapp.', function() {
    grunt.task.run('copy-assets', 'compress-assets');
  });

  grunt.registerTask('default', 'Minify and copy all assets into webapp.', ['create-assets']);

};