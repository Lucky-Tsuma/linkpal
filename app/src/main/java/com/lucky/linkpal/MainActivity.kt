package com.lucky.linkpal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lucky.linkpal.utils.SafeClickListener.Companion.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_log_in.setSafeOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        button_create_account.setSafeOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        about.setSafeOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        contact_us.setSafeOnClickListener {
            val mails = arrayOf("linkpal97@gmail.com")
            val mailIntent = Intent(Intent.ACTION_SEND)
            mailIntent.data = Uri.parse("mailto")
            mailIntent.type = "*/*"
            mailIntent.putExtra(Intent.EXTRA_EMAIL, mails)
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Linkpal support")
            mailIntent.putExtra(Intent.EXTRA_TEXT, "")

            if (mailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mailIntent);
            }
        }
    }
}
