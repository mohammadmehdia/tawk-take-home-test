package to.tawk.tawktotestapp.extensions

import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.service.autofill.FieldClassification
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import to.tawk.tawktotestapp.R
import android.text.method.LinkMovementMethod

import android.text.Spanned

import android.util.Patterns

import android.text.SpannableString

import android.text.TextUtils
import android.text.style.StyleSpan
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern


@BindingAdapter("avatar")
fun ImageView.avatar(url: String?) {
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .skipMemoryCache(false)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(this)
}

@BindingAdapter("negative")
fun ImageView.negative(negative: Boolean) {
    if(negative) {
        colorFilter = ColorMatrixColorFilter(floatArrayOf(
            -1f, 0f,  0f, 0f, 255f,
            0f, -1f,  0f, 0f, 255f,
            0f,  0f, -1f, 0f, 255f,
            0f,  0f,  0f, 1f,   0f
        ))
    } else {
        colorFilter = null
    }
}

@BindingAdapter("visibleIf")
fun View.visibleIf(visible: Boolean) {
    visibility = if(visible) View.VISIBLE else View.GONE
}

@BindingAdapter("nightModeStatus")
fun ImageView.nightModeIcon(isNightMode: Boolean) {
    setImageResource(if(isNightMode) R.drawable.ic_night_filled else R.drawable.ic_night_outlined)
}

@BindingAdapter("boldNumbers")
fun TextView.boldNumbers(str: String?) {
    text = str
    str?.let { it ->
        val ss = SpannableString(it)
        val matcher: Matcher = Pattern.compile("\\d+").matcher(it)
        var start = 0
        var end = 0
        try {
            while (matcher.find(end)) {
                start = matcher.start()
                end = matcher.end()
                val span = StyleSpan(Typeface.BOLD)
                ss.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        } catch (ignored: Exception) {}
        text = ss
    }

}