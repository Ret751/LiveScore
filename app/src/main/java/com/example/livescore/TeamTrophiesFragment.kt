package com.example.livescore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.livescore.databinding.FragmentTeamTrophiesBinding

class TeamTrophiesFragment : Fragment() {
    private var _binding: FragmentTeamTrophiesBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(teamId: Int, teamName: String) =
            TeamTrophiesFragment().apply {
                arguments = Bundle().apply {
                    putInt("teamId", teamId)
                    putString("teamName", teamName)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamTrophiesBinding.inflate(inflater, container, false)
        // TODO: 트로피 수상 내역 전체 로드
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
