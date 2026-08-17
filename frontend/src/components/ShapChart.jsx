import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, Cell,
} from 'recharts';

export default function ShapChart({ explanation }) {
  if (!explanation || explanation.length === 0) return null;

  // Build chart data — shorten long labels for the axis
  const data = explanation.map((item) => ({
    name: item.label.length > 40 ? item.label.slice(0, 40) + '…' : item.label,
    fullLabel: item.label,
    value: item.shap_value,
  }));

  return (
    <div className="w-full" style={{ height: Math.max(240, data.length * 44) }}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={data}
          layout="vertical"
          margin={{ top: 8, right: 40, left: 10, bottom: 8 }}
        >
          <CartesianGrid strokeDasharray="3 3" horizontal={false} />
          <XAxis type="number" tick={{ fontSize: 12 }} />
          <YAxis
            type="category"
            dataKey="name"
            width={220}
            tick={{ fontSize: 11 }}
          />
          <Tooltip
            formatter={(value) => [value.toFixed(4), 'SHAP value']}
            labelFormatter={(_, payload) =>
              payload && payload[0] ? payload[0].payload.fullLabel : ''
            }
          />
          <Bar dataKey="value" radius={[0, 4, 4, 0]}>
            {data.map((entry, i) => (
              <Cell
                key={i}
                fill={entry.value >= 0 ? '#22c55e' : '#ef4444'}
              />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}