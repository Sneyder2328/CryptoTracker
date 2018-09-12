/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneyder.cryptotracker.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import androidx.os.bundleOf
import debug

class SingleChoiceDialog: DialogFragment() {

    companion object {

        private const val ARG_TITLE = "title"
        private const val ARG_LIST = "listItems"
        private const val ARG_CHECKED_ITEM = "checkedItem"

        fun newInstance(title: String, listItems: Array<String>, checkedItem: Int): SingleChoiceDialog {
            return SingleChoiceDialog().apply {
                arguments = bundleOf(
                        ARG_TITLE to title,
                        ARG_LIST to listItems,
                        ARG_CHECKED_ITEM to checkedItem)
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE) ?: throw Exception("No value passed to argument ARG_TITLE in SingleChoiceDialog")
        val listItems = arguments?.getStringArray(ARG_LIST) ?: throw Exception("No value passed to argument ARG_LIST in SingleChoiceDialog")
        val checkedItem = arguments?.getInt(ARG_CHECKED_ITEM) ?: throw Exception("No value passed to argument ARG_CHECKED_ITEM in SingleChoiceDialog")

        return AlertDialog.Builder(context!!)
                .setTitle(title)
                .setSingleChoiceItems(listItems, checkedItem) { d, item ->
                    debug("onItemSelected ${listItems[item]}")
                    getRegisteredListener().onItemSelected(d, item)
                    Handler().postDelayed({ d.cancel() },200)
                }
                .create()
    }

    private fun getRegisteredListener(): SingleChoiceDialogListener {
        return when {
            parentFragment is SingleChoiceDialogListener -> parentFragment as SingleChoiceDialogListener
            context is SingleChoiceDialogListener -> context as SingleChoiceDialogListener
            activity is SingleChoiceDialogListener -> activity as SingleChoiceDialogListener
            else -> throw Exception("You must implement SingleChoiceDialogListener in your activity, context or parentFragment associated with this DialogFragment")
        }
    }

    interface SingleChoiceDialogListener {
        fun onItemSelected(d: DialogInterface, item: Int)
    }
}