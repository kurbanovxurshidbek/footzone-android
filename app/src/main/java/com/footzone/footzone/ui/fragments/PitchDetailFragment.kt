package com.footzone.footzone.ui.fragments

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.footzone.footzone.CalendarDIalog
import com.footzone.footzone.R
import com.footzone.footzone.adapter.CommentAdapter
import com.footzone.footzone.adapter.CustomAdapter
import com.footzone.footzone.databinding.FragmentPitchDetailBinding
import com.footzone.footzone.model.Comment
import com.footzone.footzone.model.Pitch
import com.footzone.footzone.model.TimeManager
import com.footzone.footzone.utils.Extensions.changeTextBackgroundBlue
import com.footzone.footzone.utils.Extensions.changeTextColorGreen
import com.footzone.footzone.utils.Extensions.changeTextColorRed
import com.footzone.footzone.utils.Extensions.changeTextColorYellow
import com.footzone.footzone.utils.Extensions.hideBottomSheet
import com.footzone.footzone.utils.Extensions.setImageViewBusy
import com.footzone.footzone.utils.Extensions.setImageViewisBusy
import com.footzone.footzone.utils.Extensions.showBottomSheet
import com.footzone.footzone.utils.GoogleMapHelper.shareLocationToGoogleMap
import com.footzone.footzone.utils.KeyValues.PITCH_DETAIL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*


class PitchDetailFragment : Fragment() {

    private lateinit var binding: FragmentPitchDetailBinding
    lateinit var adapter: CustomAdapter
    lateinit var adapterComment: CommentAdapter
    lateinit var pitch: Pitch
    private lateinit var bottomSheet: View
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    var times: ArrayList<TimeManager> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitch = arguments?.get(PITCH_DETAIL) as Pitch
        Log.d("TAG", "onCreate: $pitch")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_pitch_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPitchDetailBinding.bind(view)
        bottomSheet = view.findViewById(R.id.bottomSheet)
        initViews()
    }

    private fun initViews() {
        allTime()
        //
        refreshAdapter()
        refreshCommentAdapter()
        binding.rbRate.setIsIndicator(true)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.linearNavigation.setOnClickListener {
            requireActivity().shareLocationToGoogleMap(41.23255,69.18630)
        }

        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.hideBottomSheet()

        binding.btnOpenBottomSheet.setOnClickListener {
            if (sheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                sheetBehavior.showBottomSheet()
            } else {
                sheetBehavior.hideBottomSheet()
            }
            controlBottomSheetActions()
        }

        binding.cordLayout.setOnClickListener { sheetBehavior.hideBottomSheet() }

    }

    private fun refreshAdapter() {
        adapter = CustomAdapter(getPitchImages())
        binding.recyclerView.adapter = adapter
    }

    private fun getPitchImages(): ArrayList<String> {
        return arrayListOf(
            "data:image/jpeg;base64,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",
            "data:image/jpeg;base64,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",
            "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAoHCBYWFRgWFRYZGBgaHBocGBkcHBoaGhwYGBgaGhgYGBwcIS4lHB8rHxoaJjgmKy8xNTU1GiQ7QDs0Py40NTEBDAwMEA8QHhISHjQhISE0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NP/AABEIAKsBJgMBIgACEQEDEQH/xAAbAAACAwEBAQAAAAAAAAAAAAADBAECBQAGB//EAD0QAAEDAgMECAUEAQMDBQAAAAEAAhEDIQQxQRJRYXEFEyKBkaGx8AYUUsHRMkLh8RUzYpIjU3IHY4Kisv/EABgBAQEBAQEAAAAAAAAAAAAAAAABAgME/8QAIBEBAQADAQACAwEBAAAAAAAAAAECERJREyEDMUEiYf/aAAwDAQACEQMRAD8A+bmmqlhR2ojWLs82yrQrhiabRlT8uVU3CwYp2Uy2mpNPggU2VYU0yKSu1iBPYU9WnupUGijNpMMUdWmzRU9Ui7J7CnYTnUqworQQ6tT1ae6lcKaEIdWu6tP9Uu6lTQRFNd1afFBd1KoS6tdsJ4UFPy6LCGwrCkn24dW6lTRWd1Sk0k/1Kg0eCqEOrUdUtDqFHy6mktZ/VruqWj1C4YdNG2d1ajq1p/LqPl1DbN6td1a0Th1R1IITbPNNVLU6aRUfLqNQiWlQndgc1yjW1TQIXCmVXDdJHJ42hvFj4ZFa+FFOpZrhO42Pgps0SohNhgWlT6JO5Fr9GObor1DlmNw4Ko7DxotVmEduVepIKbOWYKCscLwWn1Q3InUWsmzTH6ghXazeFptpg5ojcKFdljK+X3KDhlqvwsZKrWHcrtnlmCmrjDrRdhp0RKGFTpdMs4ZV+VM2C9CMGpODU6SMD5Qrm4Urf+U1AQ/kzOSdLIyG4Xgo+TK9AzAncmBguCdLp5tuDsrtwfBeg+V4KW4XgnUZsrz/AMoo+V4L0JwvBVOF4J1E5rzxwvBccNwXoPleCG7CJ0mmH8rwXfLLdGEAVH0Y0U6XljNwin5Va7cK52kIzMBCnS8sMYVR8twXoPlApZhJyCnTUxecdhDuVRgQvSvwBm6I3AgA2U6XmvIPwp/a1DOC+peoq4MngFjY7pGhSkbW27cy/i7IK9Joj8tuClZWL+IKhPYhg4AOPeSuT7NMFhTdB+9ItKZoyUbr2vw9i60jYdtt1a7tEDgcx5r3NcskCowssCXZsuJzFx3heD+E6jKTxVe4wy8AxJ3SLr0vxN8RtrPa0gt7LC0tP1NBII5lZv7WXUb2H6OY8SwhwjMEEeSTf0PJyUfCmHa7ac5xY7ZcQWmNoASSQN3FO4bpKpt7IY17fB8b5aCD4BRrcs+2NiOhyClKmCI0XrcRiqO0Q93VmSIeNmY1ByjnCLWwgIBaAQRYiDPgrs5l/TwzqG8KWUiMl6mt0cNyTf0duTpOGZTpzxRDhhqE0MGRomWUyBvTZyzBgkHq9g8FvMoncoq4HatCbOCNFk6q5Yr/ACT2ZXCq9r9ybOVW0wDmjillZLhrpuITlNxjNNrMVhSUmkdys1+9Hpuk5ptqYwscPvhQ6mmngDVUe9uQumy4wsaKoaSJ1JJuYRWsaM7ptOC3UruoTzyEF7joE2cQucOqODAiHDudnKNT6O3qbTgoHjQIgpuOkLTpdHJ59Kmxm09zWDe4geuam2uPWNR6OnO6Zw+DM5J1mPYGuNNjnwJkCG5gZkT5JPCY19Z4a53V3vsZci43nlCfZ/mOxTabP9R7WbpNzyGZSeIxBDHupUi6AIe6zTJiwFz3wvPfEmH6qo6STc9rU3yB/KN0J8R7NOpTIGwQAJvcmJJV0xcvvX6eR6b6QqvJFR5I+hvZb3gfeV52oe5bfS4BcYtw07lhVQtxzBcVyhy5GgmNTeHYl6bJ9b2smGOtb3CJadficmjJNYjGdoEZwL8hCx2vkoxeNUR634Y6Vc2oST+1wvxELWwnTfVVRs37VibwCdAvEYCpBsU4+oZBUsNvo+I6Yp13u22tBDiNu0ETbaGtlPSmwxjHYZ+w/Zk3Ldq5ygwb6GV8yfjXbRunqnSruzeYEJprp6Gl8XV2HZqBr/8AyGy7/k23ktXDfGGHdG217DvHbb4i/kvDvxYcEm945ck5lJnZ/X1/CY7D1f0VabidNoB3/E3WicBaYXxPD1yMr84IK9tgcd1eGDm1XNeXWph5DYjw7vNZuOm8fy7/AHHs24MxYKraR1CxOi+mcU9j3h7HbDZ2XtE5idnYjaSQ/wDUEtMPoNO8h5b5Fp9VNZOkzx/v09a1m8K5wrTosDA/HWGeYLHtP/wPh2p8luf5/DSA57mEgGHMcM+QUvXjXWN/qanRzTwQh0YE7/l8NAPWNAzkhzfUKo6YwxyxFP8A5tWd1esSn+OVDgVpf5TDW/69O/8Avb+Vw6Uw2lVh5OB9E6qdYsl2BVhg7JzE9OYVh7VVo7nH0CXq/EmGa0uG24TmGGPF0LW74bx9UZgt6Mzo8cVgYn4/oNMMpvceJa0eRKBS+O3vdsspMbORJc/yGymsvE7wj1jujgNF1TBtaJgAbzksTpXpXEM2SagLS1pOxstAkTcxtA8Fg/Eb9vZdTeah2WlweSXCRm0OzCSVL+ST+PSYvprDU/1VWE7mds//AFlYuJ+NWD/TpudxeQ0eAk+i8NXJm8hcx4W5jHK/lyv6+nqGfE2Jquhrgwf7RsiOLjJ81sV6lE0Wy+am0ZcZIJgZEme8rwj8VAtZR80SwCdSfRXlnq/37fQOjeljTo1TFoEA32jIknhE5LDZ0sHVGFp2YcDszbPRZTceRSLd4+4WXTedoEJouTY+JcW4vLpkOuRpKxGVxsujWPVM4qrtAglY7nxkrIzaLinbQnVZjwdU4auluSUqtmY5/lAuQFyq5ciuY4KwO5UaNAR6KzmuFyDdES0blZhkqofp396YY8ROvLNEq1B0HhlP3KeZP6RJMmYuIbmRvyJSDItl6JhrQD2js+ZFpHjKorUff39leo/Kyq5nHWw4KWsLm2zQS13FWk81L+j6zQSaboGsTnYZIZcRYtjuVQxREJh2NOyefoEiKkWBKqXxKaVsYPph7Q7tHIR3OB+yHjMSH9rfmsxj87KzHiw9Dv0U0bGw1TYdOZGW7wXpMV0x1mwahlzWtEixgAZkLyzgJ1Cvtjeliyvb9J9Oh9CmxwFmm4sRcxO/TNecpvgztGPNIVKshonT7qjXcVJiW7etr9JU302M2NhwB7QNzc/qEekIfR+KFF4eTt7hPZ79682alhdS2tfNOTb0fxF0iyrVe6Nm9oyjlogs6WPy76RjZLmkDkDfnxWBWqbRJlS18N1gn0z9U0bUeRP2T2CrXG71SGyDeCjMqADJUamP6Vc587WgEaWCWxWMJcHNsQB5BJVqt8gqmpruU0bPurbf6hff+UvUZxCVFXmZ5q7dszDXHfYmOauhLo3olN/ZFlD8DU2doscBrNs8s7qrxEDVAWrVItlpxHNKbZzndzv781JfBJPMc9DKDtySbk3JPM3MqAwfPG1/53JSq68aogfC7E1AR2RzMKgO0RYRv0mYjPOIKpYgZ7U8IjvGaqX5RAVXtdGscreKgHUjT8rlRyhFLgJjEYl7yC9xJDWtHBrRDW20AHkqU6RMwQIE5gCAJzNu5X6vc7Tlpf8ACkW0MPdz5oja28eCs+lsmAZNjIIIuJ0Vm0Z0uqzuOY9vEJvDU9shrDLjYDUk5BKNZOl910QM2YNweGiBurSc1xDoBBIOViDBHioZUc2bTKWIm5J7/Vc9zzHaJjLkrpDdLFPbk5wHBx04Smv8xUiHOLsrOAPmswPd7hHoVmgO22FxIhpBiHbzGetkB8RjtsAFjBeZa2DyMIVdoBBCXc5uYafFcKzTofFAWOKlkcFWg1rzG1sTq7Ic4VNobz4IDtBH6vYV3Dhug5ZDdvKEakgS4cFam0uBIIsJPLLVB15JC7PmVRjoMyPfepdUm8j33qgvOVJcRly/koQa6ATAacjvjOCrMqRq2/FRRRI5jVRs2789DkYQmOvmOF1esC0lriAQbjODqiCbDtbDXgpj+tUE1bfqsr0g17o2tmf3OkDvKKlzVdmzsmTn/SVc9u4oTqgmIPkg1W9I2gMY3iGgnxOS5/S1Q2L3ARkIHoEjSe0R2ScpndqrYirLiWDZboDBIHE6oyI7FunaJc7mSfVQ6s4nai/oln1HkQCqtc7Z2S627iml2dw1Co+WsuYLiLfpA7R5AFJveBmZVWuIyJ7pFlAp55+zCCOtAyBVHV5yAV3MEwL8eHLkqupwYFsr896GwXPdlPgrfMvDDT2jsFwcW6FzQQDzgoj2lpBjX90EHdI1BS+zbvuYy7/eSEoJHFcpIE3v3wuUXaGMdGVuSIJH4XdH9IPov22Oh0EXAIgiCINskE1TvRqwxtu4Ija2RAPFKNqHge4J3E41r2sDWMYWiHET2z9btx5WTbNiWPF7Ed2mqs2o0SAT3ixsc0q2twHifym6lansN2Wu277faGyR+3ZEc5lE0vTexxv2ROVjDeBJExuVwW7UgyPskWvB3+X4R8SykA3Ye5xI7ctA2XagXuOKpoU07mI3onVDISDbOBa8/ZL4QMLgHPLZMbRFhxKvVLA4gPmLA7JvxCIsxhIsfwpdTyiBKlrBsFwqNzA2bhx4gblTYcS1ocLmBJi/Mqieq/OWi51MSYII35d3iuqSCW7TdxuPIohL2hrtoRm3tC0G9tLqbFG4cDwm1+QF1UMj390Ru2WmIIFzcWk5+K6kXO7IAJMgb5O6DmgEaXAqxoxx9VBe4GLIlbaFiINjxFlRxpGCDYtMFpmb7mxw8wquw2/S/cue94bMWORveLGCupue4EhlgACdBunmg5jBN+zx/AVnMmbybZTrFrxlfwUsY95gDaMWuDYXgbgoc9xtI8R+VNiOptOt1bqTEkW0MT3BRWL2P2XANIz3iVZ9JwaHGIcTBkbRIzsrsWFETBMDWInwUCmIH23eCihTc+QHNsC4yQLDPmeCGT/ut3qArqMGCbm4/SZtMZwoc252RO4ndxiyjEUwyJeHFwBOySY4O4jcowjGPJBfsQCZO1BMfptqckVZkSTAsqsAM9oCNCYPcNUtttGnkmQKextbR6yY2Nns7P1Tv4IiNto+nLOxzvoYB55IXXtmXEn1Ji3nCE4xofII+JFIMZstdt325cC3hsxwzQCbWaMhy+8qDVEZTv8AOEIOjIN77p52PHVBnVs25J6zWPovaNZz7lNrCPXH7dyoGudoSoc92rvD8ouCx76T21GP7bbtkAgHeQbHPVFgVRjibtyG6O8qEPE1XPcXE3Jk8yuRdFpTVBoNvfNR8g/6Ualgniezos9RcssfUsoCTkfLvN0enRaYixAvJnaMnKw2REWvkhtwL/pPkmGYZ4H6J4+ynU9YuU9TVwNgQJ/nJCGG013HeEwKD79jz/lc/DPggUwOO1f1TvH06noTsGA2bh1w4EQJtAadTnuiFWnhr+EhWHR9U5g+Kbo4J4zZtHeXfynePqdYqYjo6ILRn/aCMCTMEGMyD6Tcp8YWpH+mf+R8M0Kt0dVP6WFvJxunyY+pcoUfhCIi++dDMRPvNH/xx2C7UblZnR9UGSyd4Jz5o7aFWCAwAbts/lPkx9Z6hJ3R5kyRacpzGmXJUdhSJjI2yzi9k3VwdVxkN2d8OJnxKtR6OqzJG1O9xT5MfV6lBdgCGtMZ2Mb7oLcKRf7ZZrWGCqxGxbMdsoJ6JxBJ2QWg6B1vVT5MfTqM9uFJIEC53X3IlTCEG4kCJPPIStBnRNYADYBI12jv4Ktfo6sZGzc67ROXeny4+nUZ3UmMhwmYggmRoiNwpgwOYjwTDOiq+oJG7asmG9HVTPZic+0fyny4+w6jKZTIuAPA+CnqDIiMhmLgwJJCef0LXJlojgHLmdE1xmyTv2j+U+TH1NlKtF0AmDOZ4W3qhaSDbONL62F8sk+/o6rkaZ4Xdbklm9GVxv8ANX5MfTqKNouEib2yAPipqYV/DKTkLDVMswlQfsvqdo3VqmDqOB/6ZnftExyunyY+nU/hAUHHforDDZAEz9J9BHqijoyv9L+5FGCqAf6Tp39pO8PSZQEYEhm0SZKB8te5yN/eUJ19Gr/2XeL0jUwVUk9hwG68J8mPp1BHYTJwaL5t7UC+h3ZaoIw85XPHh+USnhXtMljiNxlENN3/AGj5/lO8fV3PUHB7Lbi/3KWqUgLZ6CHTBMTkEdzHT/pE+P5SlbDvkkMI4Jc8fSWeuDSDYiRpvkQR4JOqUfqHA/pKFVouJyTqN42b/YErlfqXblybdd4vStcEXbH0+/FCE8EVjF5q8GxWVR9HvxRQ8fQFDKY39yOyiNNFzqboQefoCI17tGt8P5RmUQEzTMDcpasLML/o8kcMf9CbY7v38EZrwd2XvyXO10xx/wCkW0nnQKzsI/UDw/laLR4FGadFK1MYy24HhPgrtwA+kLTm+igu/kFTda5xItwY+geCMyhH7R4Ijq0IZqknsqbXeMW6vgEN7vpbKu12+PYVusHBTZ1CjmvP7RyhQWEftHgntv2PRV2x78UPonsHVg8P5VhTP0hNbQm3sq5fBCG4TDXDJoXOe/VgPcmX1R3LnOEZhInU9Cpmc2x3Igoz+0KNsTHu2fcpa+MjyV2syipwoP7B4KHYQfQPBMMxPI+v8ogqi/sps/yQdgho2FAwh09PwVoF4VXHiqaxZzsM/h5oT6VQaDyWrOqo6I/ryU2ajKl4/aPAKJd9DfBaznDKfG3eoFT3aZ5qbXU9ZBadWNPchPZ/7bVsbYy9UN4G4KzKsXGPP1GD6ErUpj6D7716CpTbu80k+g2912xzcssWE+mN0Llp1sNexXLfbGme1wmxRAQl2kcTvj1VmPA9+9y1Sm2OG9Ga/kltuNMveisXZe7rNjJoG9j5dyOx9vdgkGOjK+g9c+5GZUMmfeRj0WbA9Td7vvRmv3LM+aHKxG9Q7FAjOD63hTlrps7ZgnPK2XuygYwC5KxxjIy89+XqqOrSJJBz5e81m4lz0234tpiJ5+Fwo+ZLhFzJ0tlclZDHkAeV7KzMQ4cN/GXBZ5rF/Ja0nOsJsZjOSb+wu2jO86c/vvSdDFTru42tdXFcGTz8c05OjkgEcvGfurbRjnPoD6lKHFQ0yLkec5/ZXdVBAI0kyYtMfhNLsbbm+70HsqHO/THePfuyX6yxmLHeNTc8lZr5yJj7FTRujCplnPLgofW38J3DT7hUp1cxcEi2+Yv3W8ktUrZ7QvFu8eeQKaLTL3xnPC3L33Lm1SMznbPWeOWYSrXyL8O/ZQzWkyN32H4Tli2neuy53PAFc2oSAdMvyUqXi830ytOasXRcWEkDMXyKuiWn2VBAggjKfvCGXk/f37ySba9ogWnKN+nvVR10HZkmZOuWX8KctdmDVc39OmR4T/SI3FG8+95SVSuO/I+EwgnElpgfp9ZEn3xV5O76134xsQN3nzKsyu02B9n1P5WN8xNj5ZZqjqwA7JInXQ8R5Jqr3Wy2qHA6d+/Luy8UV5yvccjOhyWH82W5ix47h/aMzF7ybGOE23+Cmmpm0zVjPPgON/fFUe8fcH8pLrgHTcz+Nb7tOCq+uYEzu0P9KyNTMxWq65JV7pvv9fcID8TIk8IG6LXQX1gfHnGcrcxLlsQvlclKhi4jj39/Bct8oUpPtJU7cn0tp9kEWj3uRGLqthhlQa/3OaNhy3eYj76JBuR/8fumqY+33UsZs0NUIB3a5bzHofdlT5oaX9jNLOdc+96q/wC/2U0uoYc8m/PL7e9VzH2JPuEBpg+Ks/I9/o78DwV0mjDqgvOS4Vsh580u3LvRW5t7/QJpmxd1Tw9/hFp1LROk79x+yWGZ971d2SljNn8MF+7X3by8FzKmu1yHp74IDMu5XoHLmpYlhlj4sTPP7eSo+vEFvsIX7fe5QbNPI/dSYxZDD6kjndcMUR2R3+aTZn3K1TOefqlxho2ytOsXnP08clXrt97zfI/wk2GAPeigOnz9AnEaO1MQTlYHdnYhSHkEmQDew1v/AEgDNvMINbL/AJfZXiEkOvq6zY2OXeYQm1IbnnkdJkfZD3e/2odfM8z6pxGdG6Tza947hn46KKzza5m/KJ9+CVDza+hR3XZfTu14Jr7NLjEDWNIz7rqr3y05T9oOiTn9Pcocbn3oE4ho9t2gG4IEjefwAqVHfpgAkbiL3MevkgUvz6EqaN6UnPbAnh27KctSGRUFxYSCRyE69xQTWDRYmZN9bC0eJSp+7fNpQarzJvu9E4iyGnVXAxJE5xy8/wClBquIvcZnnfNKueZF9SiVcu8f/o/hNLoVuIJF415wqdZx9m6E51jzSoeZz9wtTGLJs443zXIDjkpV0un/2Q=="
        )
    }

    private fun refreshCommentAdapter() {
        adapterComment = CommentAdapter(getComments())
        binding.recyclerViewComment.adapter = adapterComment
    }

    private fun getComments(): ArrayList<Comment> {
        val items = ArrayList<Comment>()
        items.add(
            Comment(
                "Jonibek Xolmonov",
                3.5f,
                "18.05.2002",
                "Measure the view and its content to determine the measured width and the measured height. This method is invoked by measure(int, int) and should be overridden by subclasses to provide accurate and efficient measurement of their contents."
            )
        )
        items.add(
            Comment(
                "Odilbek Rustamov",
                2f,
                "11.05.2002",
                "CONTRACT: When overriding this method, you must call setMeasuredDimension(int, int) to store the measured width and height of this view. Failure to do so will trigger an IllegalStateException, thrown by measure(int, int). Calling the superclass' onMeasure(int, int) is a valid use."
            )
        )
        return items
    }

    private fun allTime() {
        times.add(TimeManager("00:00", "01:30", "band"))
        times.add(TimeManager("02:00", "03:30", "band qilinmoqda"))
        times.add(TimeManager("06:00", "07:30", "band"))
        times.add(TimeManager("10:00", "12:30", "band qilinmoqda"))
        times.add(TimeManager("13:00", "14:30", "band"))
        times.add(TimeManager("16:00", "16:30", "band qilinmoqda"))
        times.add(TimeManager("20:00", "20:30", "band"))
        times.add(TimeManager("21:00", "23:30", "band qilinmoqda"))
    }

    /**
     * this function, controls the time it takes to apply to the stadium
     */
    private fun controlBottomSheetActions() {
        val timeList = resources.getStringArray(R.array.timelist)
        var boolStart: Boolean = false
        var boolFinish: Boolean = false

        binding.bottomSheet.ivCalendar.setOnClickListener {
            val dialog = CalendarDIalog { date ->
                binding.bottomSheet.tvDate.text = date

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.showCalendarDialog(requireActivity())
            }
        }

        binding.bottomSheet.startTime.minValue = 1
        binding.bottomSheet.startTime.maxValue = 47
        binding.bottomSheet.startTime.displayedValues = timeList
        binding.bottomSheet.finishTime.minValue = 1
        binding.bottomSheet.finishTime.maxValue = 47
        binding.bottomSheet.finishTime.displayedValues = timeList

        binding.bottomSheet.startTime.setOnLongPressUpdateInterval(8000)
        binding.bottomSheet.finishTime.setOnLongPressUpdateInterval(8000)


        binding.bottomSheet.startTime.setOnValueChangedListener(NumberPicker.OnValueChangeListener { numberPicker, i, i1 ->

            checkVibrationIsOn(requireContext())

            for (time in times) {
                if (time.startTime == timeList[i]) {
                    boolStart = false
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                    if (time.type.equals("band")) {
                        binding.bottomSheet.ivStartImage.setImageViewBusy()
                        binding.bottomSheet.ivCaution.setImageViewBusy()
                        binding.bottomSheet.tvCaution.text = "Boshqa user tomonidan band qilingan."
                        binding.bottomSheet.tvCaution.changeTextColorRed()
                        break
                    } else {
                        binding.bottomSheet.ivStartImage.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.text =
                            "Boshqa user tomonidan band qilinmoqda!!!"
                        binding.bottomSheet.ivCaution.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.changeTextColorYellow()
                        break
                    }
                } else {
                    boolStart = true
                    binding.bottomSheet.ivStartImage.setImageResource(0)
                    binding.bottomSheet.ivCaution.setImageResource(0)
                    binding.bottomSheet.tvCaution.text = "Bu vaqtda bo'sh joy bor"
                    binding.bottomSheet.tvCaution.changeTextColorGreen()
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                }
            }
        })

        binding.bottomSheet.finishTime.setOnValueChangedListener(NumberPicker.OnValueChangeListener { numberPicker, i, i1 ->

            checkVibrationIsOn(requireContext())

            for (time in times) {
                if (time.startTime == timeList[i]) {
                    boolFinish = false
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                    if (time.type.equals("band")) {
                        binding.bottomSheet.ivFinishImage.setImageViewBusy()
                        binding.bottomSheet.ivCaution.setImageViewBusy()
                        binding.bottomSheet.tvCaution.text = "Boshqa user tomonidan band qilingan."
                        binding.bottomSheet.tvCaution.changeTextColorRed()
                        break
                    } else {
                        binding.bottomSheet.ivFinishImage.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.text =
                            "Boshqa user tomonidan band qilinmoqda!!!"
                        binding.bottomSheet.ivCaution.setImageViewisBusy()
                        binding.bottomSheet.tvCaution.changeTextColorYellow()
                        break
                    }
                } else {
                    boolFinish = true
                    binding.bottomSheet.tvOccupancy.changeTextBackgroundBlue(boolStart, boolFinish)
                    binding.bottomSheet.ivFinishImage.setImageResource(0)
                    binding.bottomSheet.ivCaution.setImageResource(0)
                    binding.bottomSheet.tvCaution.text = "Bu vaqtda bo'sh joy bor"
                    binding.bottomSheet.tvCaution.changeTextColorGreen()
                }
            }
        })

        binding.bottomSheet.tvCancel.setOnClickListener { sheetBehavior.hideBottomSheet() }

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.frameWrapper.setBackgroundColor(Color.parseColor("#40000000"))
                }
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.frameWrapper.setBackgroundColor(Color.TRANSPARENT)
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })
    }

    /**
     * this function, gives the NumberPicker sound and vibrate
     */
    fun checkVibrationIsOn(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
            val v: Vibrator =
                requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(70)
        } else {
            val mMediaPlayer = MediaPlayer.create(context, R.raw.mouse_1)
            val audioManager =
                requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.start()

            val handler = Handler()
            val t = Timer()
            t.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Runnable {
                        mMediaPlayer.stop()
                    })
                }
            }, 500)
        }
    }
}