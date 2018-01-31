package com.wongxd.absolutedomain.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.LogInListener
import cn.bmob.v3.listener.OtherLoginListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.orhanobut.logger.Logger
import com.tencent.connect.UserInfo
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.utils.utilcode.util.BarUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.KeyboardUtils
import com.wongxd.absolutedomain.data.bean.UserBean
import com.wongxd.absolutedomain.event.LogStateChangeEvent
import com.wongxd.absolutedomain.fragmenaction.BaseBackFragment
import com.wongxd.absolutedomain.util.SystemUtils
import com.wongxd.absolutedomain.util.TU
import kotlinx.android.synthetic.main.fgt_login.*
import kotlinx.android.synthetic.main.layout_signin.*
import kotlinx.android.synthetic.main.layout_signup.*
import me.yokeyword.eventbusactivityscope.EventBusActivityScope
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject

class FgtLogin : BaseBackFragment() {

    private var isSigninScreen = true
    private lateinit var tvSignupInvoker: TextView
    private lateinit var llSignup: LinearLayout
    private lateinit var tvSigninInvoker: TextView
    private lateinit var llSignin: LinearLayout
    private lateinit var btnSignup: Button
    private lateinit var btnSignin: Button

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)

        BarUtils.setStatusBarVisibility(activity, false)

        llSignup = mRootView.findViewById<View>(R.id.ll_signup) as LinearLayout
        llSignin = mRootView.findViewById<View>(R.id.ll_signin) as LinearLayout
        llSignin.setOnClickListener { KeyboardUtils.hideSoftInput(it) }
        llSignup.setOnClickListener { KeyboardUtils.hideSoftInput(it) }

        tvSignupInvoker = mRootView.findViewById<View>(R.id.tv_signup_invoker) as TextView
        tvSigninInvoker = mRootView.findViewById<View>(R.id.tv_signin_invoker) as TextView

        btnSignup = mRootView.findViewById<View>(R.id.btn_signup) as Button
        btnSignin = mRootView.findViewById<View>(R.id.btn_signin) as Button



        tvSignupInvoker.setOnClickListener {
            isSigninScreen = false
            showSignupForm()
        }

        tvSigninInvoker.setOnClickListener {
            isSigninScreen = true
            showSigninForm()
        }


        btnSignup.setOnClickListener { doSignup() }

        btnSignin.setOnClickListener { doSignin() }

        iv_qq_signin.setOnClickListener { doQQSignin() }

        showSigninForm()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!BarUtils.isStatusBarVisible(activity))
            BarUtils.setStatusBarVisibility(activity, true)
    }

    private fun showSignupForm() {
        val paramsGuideLine = guideline.layoutParams as ConstraintLayout.LayoutParams
        paramsGuideLine.guidePercent = 0.15f
        cl_login.requestLayout()

        val translate = AnimationUtils.loadAnimation(context, R.anim.translate_right_to_left)
        llSignup.startAnimation(translate)

        tvSignupInvoker.visibility = View.GONE
        tvSigninInvoker.visibility = View.VISIBLE

        val clockwise = AnimationUtils.loadAnimation(context, R.anim.rotate_right_to_left)
        btnSignup.startAnimation(clockwise)

    }

    private fun showSigninForm() {

        val paramsGuideLine = guideline.layoutParams as ConstraintLayout.LayoutParams
        paramsGuideLine.guidePercent = 0.85f
        cl_login.requestLayout()


        val translate = AnimationUtils.loadAnimation(context, R.anim.translate_left_to_right)
        llSignin.startAnimation(translate)

        tvSignupInvoker.visibility = View.VISIBLE
        tvSigninInvoker.visibility = View.GONE

        val clockwise = AnimationUtils.loadAnimation(context, R.anim.rotate_left_to_right)
        btnSignin.startAnimation(clockwise)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fgt_login
    }

    private fun doSignin(name: String? = null, password: String? = null) {

        val userName = name ?: et_phone_signin.text.toString()
        val pwd = password ?: et_pwd_signin.text.toString()

        if (SystemUtils.isHadEmptyText(userName, pwd)) {
            TU.cT("用户名和密码为必填")
            return
        }

        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "登录中"
        pDialog.setCancelable(false)
        pDialog.show()
        BmobUser.loginByAccount(activity, userName, pwd, object : LogInListener<UserBean>() {
            override fun done(p0: UserBean?, p1: BmobException?) {
                if (p0 != null) {
                    pDialog.titleText = "你好---${p0.nickName ?: userName}"
                    App.user = p0
                    EventBusActivityScope.getDefault(_mActivity).post(LogStateChangeEvent())
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    pDialog.setConfirmClickListener {
                        pDialog.dismissWithAnimation()
                        pop()
                    }

                } else {
                    pDialog.contentText = p1?.message
                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    pDialog.setConfirmClickListener {
                        pDialog.dismissWithAnimation()
                    }
                }
            }

        })
    }

    private fun doSignup(name: String? = null, password: String? = null, repeatPassword: String? = null, successed: (() -> Unit)? = null) {

        val userName = name ?: et_phone_signup.text.toString()
        val nickName = et_nickname_signup.text.toString()
        val pwd = password ?: et_pwd_signup.text.toString()
        val repeatPwd = repeatPassword ?: et_repeat_pwd_signup.text.toString()

        if (userName.isBlank() || nickName.isBlank() || pwd.isBlank() || repeatPwd.isBlank()) {
            TU.cT("本页数据为必填")
            return
        }

        if (pwd != repeatPwd) {
            TU.cT("两次密码输入不一致")
            return
        }


        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "注册中"
        pDialog.setCancelable(false)
        pDialog.show()

        val bu = UserBean()
        bu.username = userName
        bu.nickName = nickName
        bu.setPassword(pwd)
        //注意：不能用save方法进行注册
        bu.signUp(activity, object : SaveListener() {
            override fun onSuccess() {
                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                pDialog.titleText = "注册成功，请登录"
                pDialog.setConfirmClickListener {
                    pDialog.dismissWithAnimation()
                    et_phone_signup.setText("")
                    et_pwd_signup.setText("")
                    et_repeat_pwd_signup.setText("")
                    showSigninForm()
                    successed?.invoke()
                }

            }

            override fun onFailure(p0: Int, p1: String?) {
                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                pDialog.contentText = p1
                pDialog.setConfirmClickListener {
                    pDialog.dismissWithAnimation()
                }
            }
        })

    }


    private fun doQQSignin() {
        QQLogin()
    }


    //########################qq登录#######################################
    private val mTencent: Tencent by lazy { Tencent.createInstance(App.QQ_APP_ID, activity) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data != null)
                Tencent.handleResultData(data, loginListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun QQLogin() {
//        if (!mTencent.isSessionValid) {
        mTencent.logout(activity)
        mTencent.login(this, "all", loginListener)
//        } else
//            getUserInfoInThread()
    }

    private val loginListener = BaseUiListener()

    inner class BaseUiListener : IUiListener {

        override fun onError(arg0: UiError) {
            // TODO Auto-generated method stub
            TU.t("授权出错")
        }

        /**
         * 返回json数据样例
         *
         * {"ret":0,"pay_token":"D3D678728DC580FBCDE15722B72E7365",
         * "pf":"desktop_m_qq-10000144-android-2002-",
         * "query_authority_cost":448,
         * "authority_cost":-136792089,
         * "openid":"015A22DED93BD15E0E6B0DDB3E59DE2D",
         * "expires_in":7776000,
         * "pfkey":"6068ea1c4a716d4141bca0ddb3df1bb9",
         * "msg":"",
         * "access_token":"A2455F491478233529D0106D2CE6EB45",
         * "login_cost":499}
         */
        override fun onComplete(value: Any?) {

            if (value == null) {
                TU.t("没有数据返回..")
                return
            }

            try {
                val jo = value as JSONObject?
                val ret = jo!!.getInt("ret")
                Logger.e(jo.toString())
                println("json=" + jo.toString())
                if (ret == 0) {
//                    TU.t("登录成功  " + jo.toString())
                    val openId = jo.getString("openid")
                    val access_token = jo.getString("access_token")
                    val expires = jo.getString("expires_in")


                    mTencent.openId = openId
                    mTencent.setAccessToken(access_token, expires)
                    getUserInfoInThread(openId, access_token, expires)
                }

            } catch (e: Exception) {
                // TODO: handle exception
            }

        }

        override fun onCancel() {
            TU.t("用户取消")
        }
    }


    private fun getUserInfoInThread(openId: String, access_token: String, expires: String) {

        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "QQ登录中"
        pDialog.setCancelable(false)
        pDialog.show()
        doAsync {

            val mQQToken = mTencent.qqToken
            val userInfo = UserInfo(activity, mQQToken)
            userInfo.getUserInfo(object : IUiListener {


                override fun onComplete(value: Any?) {
                    if (value == null) {
                        onError(UiError(0, "", "").also { it.errorMessage = "没有获取到信息" })
                        return
                    }

                    try {
                        val jo = value as JSONObject?
//                        Logger.e(jo.toString())
                        val qq_header = jo?.optString("figureurl_qq_1") ?: "qq_header"
                        +System.currentTimeMillis()
                        val qq_name = jo?.optString("nickname") ?: "qq_name"
                        +System.currentTimeMillis()


                        val qqUser = BmobUser.BmobThirdUserAuth(BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ, access_token, expires, openId)


                        BmobUser.loginWithAuthData(activity, qqUser, object : OtherLoginListener() {
                            override fun onSuccess(p0: JSONObject?) {

                                //修改昵称
                                val newUser = BmobUser.getCurrentUser(activity, UserBean::class.java)

                                if (newUser.nickName.isNullOrBlank()) {
                                    newUser.nickName = qq_name
                                    newUser.update(context, object : UpdateListener() {
                                        override fun onSuccess() {
                                            uiThread {
                                                App.user = (BmobUser.getCurrentUser(activity, UserBean::class.java))
                                                EventBusActivityScope.getDefault(_mActivity).post(LogStateChangeEvent())
                                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                                pDialog.titleText = "你好---${newUser.nickName}"
                                                pDialog.setConfirmClickListener {
                                                    it.dismissWithAnimation()
                                                    pop()
                                                }
                                            }
                                        }

                                        override fun onFailure(p0: Int, p1: String?) {
                                            uiThread {
                                                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                                pDialog.titleText = p1 ?: "登录出错"
                                                pDialog.setConfirmClickListener { it.dismissWithAnimation() }
                                            }
                                        }
                                    })
                                } else {
                                    uiThread {

                                        App.user = (BmobUser.getCurrentUser(activity, UserBean::class.java))
                                        EventBusActivityScope.getDefault(_mActivity).post(LogStateChangeEvent())
                                        pDialog.titleText = "你好---${newUser.nickName}"
                                        pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                        pDialog.setConfirmClickListener {
                                            it.dismissWithAnimation()
                                            pop()
                                        }

                                    }
                                }

                            }

                            override fun onFailure(p0: Int, p1: String?) {
                                uiThread {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                    pDialog.titleText = p1 ?: "登录出错"
                                    pDialog.setConfirmClickListener { it.dismissWithAnimation() }
                                }
                            }
                        })

//                        val query: BmobQuery<UserBean> = BmobQuery<UserBean>()
//                        query.addWhereEqualTo("username", openId)
//                        query.findObjects(activity, object : FindListener<UserBean>() {
//                            override fun onSuccess(p0: MutableList<UserBean>?) {
//                                pDialog.dismiss()
//                                p0?.let {
//                                    if (it.size > 0) {
//                                        doSignin(openId, openId)
//                                    } else
//                                        doSignup(openId, openId, openId) {
//                                            doSignin(openId, openId)
//                                        }
//                                }
//                            }
//
//                            override fun onError(p0: Int, p1: String?) {
//                                pDialog.dismiss()
//                                doSignup(openId, openId, openId) {
//                                    doSignin(openId, openId)
//                                }
//
//                            }
//                        })

                    } catch (e: Exception) {
                        e.printStackTrace()
                        uiThread {
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                            pDialog.titleText = e.message ?: "授权出错"
                            pDialog.setConfirmClickListener { it.dismissWithAnimation() }
                        }
                    }
                }

                override fun onCancel() {
                    uiThread {
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        pDialog.titleText = "用户取消"
                        pDialog.setConfirmClickListener { it.dismissWithAnimation() }
                    }
                }


                override fun onError(p0: UiError?) {
                    uiThread {
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                        pDialog.titleText = p0?.errorMessage ?: "授权出错"
                        pDialog.setConfirmClickListener { it.dismissWithAnimation() }
                    }
                }
            })

        }


    }

    //#######################qq登录#########################
}
