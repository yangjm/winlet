var gulp      = require('gulp'),
    uglify    = require('gulp-uglify'),
    cssmin    = require('gulp-cssmin'),
    concat    = require('gulp-concat'),
    del       = require('del'),
    mkdirp    = require('mkdirp');

/*================================================
=            Report Errors to Console            =
================================================*/

gulp.on('error', function(e) {
  throw(e);
});

gulp.task('default', function() {
  del('dist');
  mkdirp('dist');

	gulp.src(['src/winlet_local.js', 'src/winlet_bootstrap.js'])
    .pipe(uglify())
    .pipe(concat('winlet_local_bootstrap.min.js'))
    .pipe(gulp.dest('dist'));

  gulp.src('src/*.css')
    .pipe(cssmin())
    .pipe(concat('winlet_local.min.css'))
    .pipe(gulp.dest('dist'));

  gulp.src(['src/*.gif', 'src/*.png'])
    .pipe(gulp.dest('dist'));
});
