package projects.medicationtracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import projects.medicationtracker.Models.Medication;
import projects.medicationtracker.R;
import projects.medicationtracker.Models.Dose;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    Medication medication;
    Dose[] doses;
    String timeFormat;
    String dateFormat;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scheduledDateLabel;
        TextView takenDateLabel;
        TextView dosageAmount;
        TextView dosageUnits;
        LinearLayout barrier;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            scheduledDateLabel = itemView.findViewById(R.id.scheduled_date);
            takenDateLabel = itemView.findViewById(R.id.taken_date);
            dosageAmount = itemView.findViewById(R.id.dosage);
            dosageUnits = itemView.findViewById(R.id.units);
            barrier = itemView.findViewById(R.id.barrier);
        }
    }

    public HistoryAdapter(Dose[] doses, String dateFormat, String timeFormat, Medication medication) {
        this.doses = doses;
        this.timeFormat = timeFormat;
        this.dateFormat = dateFormat;
        this.medication = medication;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        Context context = holder.scheduledDateLabel.getContext();

        LocalDateTime scheduledDateTime = doses[position].getDoseTime();
        LocalDateTime takenDateTime = doses[position].getTimeTaken();

        String scheduleDate = DateTimeFormatter.ofPattern(
                dateFormat, Locale.getDefault()
        ).format(scheduledDateTime.toLocalDate());
        String scheduleTime = DateTimeFormatter.ofPattern(
                timeFormat, Locale.getDefault()
        ).format(scheduledDateTime.toLocalTime());

        String takenDate = DateTimeFormatter.ofPattern(
                dateFormat, Locale.getDefault()
        ).format(takenDateTime.toLocalDate());
        String takenTime = DateTimeFormatter.ofPattern(
                timeFormat, Locale.getDefault()
        ).format(takenDateTime.toLocalTime());

        String schedTime = context.getString(
                R.string.scheduled_time,
                scheduleDate,
                scheduleTime
        );
        String timeTaken = context.getString(
                R.string.time_taken_hist,
                takenDate,
                takenTime
        );

        holder.scheduledDateLabel.setText(schedTime);
        holder.takenDateLabel.setText(timeTaken);
        holder.dosageAmount.setText(context.getString(R.string.dosage, String.valueOf(medication.getDosage()), medication.getDosageUnits()));

        if (position == doses.length - 1) {
            holder.barrier.setVisibility(View.GONE);
        } else {
            holder.barrier.setBackgroundColor(holder.scheduledDateLabel.getCurrentTextColor());
        }
    }

    @Override
    public int getItemCount() {
        return doses.length;
    }
}
