package br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.models.Receipt
import kotlinx.android.synthetic.main.recibo_layout.view.*


object ConvertLayout {


    fun convertResourceInBitmap(contexto: Context, resource: Int): Bitmap {
        val options = BitmapFactory.Options()

        return BitmapFactory.decodeResource(contexto.resources, resource, options)
    }

    /*
    fun creatBitmapView(context: Context, receipt: Receipt?, logo: Int): Bitmap? {
        val mInflate = LayoutInflater.from(context).inflate(R.layout.recibo, null)
        mInflate.ivLogoRecibo.setImageResource(logo)
        receipt?.let {
            loadViewData(
                context,
                mInflate,
                receipt
            )
        }
        val constraintLayout = mInflate.constraintLayout as ConstraintLayout

        return creatBitmapLayout(
            constraintLayout
        )
    }


     */
    private fun creatBitmapLayout(constraintLayout: ConstraintLayout): Bitmap? {
        constraintLayout.measure(
            View.MeasureSpec.makeMeasureSpec(
                convertDpToPixels(
                    328
                ), View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        constraintLayout.layout(
            0,
            0,
            constraintLayout.measuredWidth,
            constraintLayout.measuredHeight
        )

        val bitmap = Bitmap.createBitmap(
            constraintLayout.measuredWidth,
            constraintLayout.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        constraintLayout.draw(canvas)
        return bitmap
    }


    private fun loadViewData(contexto: Context, mInflate: View, receipt: Receipt?) {

        receipt?.apply {
            flag?.let { mInflate.tvBandeira?.text = flag }
            if (numberCard != null) {
                mInflate.tvNumeroCartao?.text = contexto.getString(
                    R.string.label_numero_cartao,
                    numberCard.substring(numberCard.length - 4)
                )
            }
            numberPOS?.let {
                mInflate.tvNumeroPos?.text =
                    contexto.getString(R.string.label_numero_POS, numberPOS)
            }
            cnpjEstablishment?.let {
                mInflate.tvCnpj?.text =
                    contexto.getString(R.string.label_cnpj, cnpjEstablishment)
            }
            nameStore?.let { mInflate.tvNomeDaLoja?.text = nameStore }
            citeUf?.let {
                mInflate.tvCidadeUf?.text = citeUf
            }
            numberEstablishment?.let {
                mInflate.tvNumeroEstabelecimento?.text = numberEstablishment
            }
            numberDoc?.let {
                mInflate.tvNumeroDocumento?.text =
                    contexto.getString(R.string.label_numero_documento, numberDoc)
            }
            numberAuthorization?.let {
                mInflate.tvNumeroAutorizacao?.text =
                    contexto.getString(R.string.label_numero_autorizacao, numberAuthorization)
            }
            datePayment?.let {
                mInflate.tvData?.text = datePayment
            }
            hour?.let {
                mInflate.tvHora?.text = hour
            }

            addressStrore?.let {
                mInflate.tvEnderecoLoja?.text = addressStrore
            }

            typePayment?.let {
                mInflate.tvTipoPagamento?.text = typePayment
            }

            value?.let {
                mInflate.tvValor?.text = value
            }

        }
    }



    fun convertDpToPixels(dp: Int): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                Resources.getSystem().displayMetrics
            )
        )
    }


}