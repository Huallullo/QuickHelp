package com.example.quickhelp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quickhelp.databinding.FragmentMapaBinding

class MapaFragment : Fragment() {
    private lateinit var binding: FragmentMapaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapaBinding.inflate(inflater, container, false)
        return binding.root
    }
}